package com.neo.twig.config;

import com.neo.twig.annotations.DontSerialize;
import com.neo.twig.logger.Logger;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class ConfigManager {
    private static Object userIdentifier;
    private static Logger logger = Logger.getFor(ConfigManager.class);

    public static void setCurrentUserIdentifier(Object identifier) {
        userIdentifier = identifier;
    }

    public static boolean saveConfig(Object object) {
        return saveConfig(object, ConfigScope.User);
    }

    public static boolean saveConfig(Object object, ConfigScope scope) {
        try {
            Config meta = object.getClass().getAnnotation(Config.class);

            if (meta == null) {
                logger.logVerbose("object does not declare metadata for config");
                return false;
            }

            String name = meta.name();
            String identifierFolder = scope == ConfigScope.User ? userIdentifier.toString() : "";

            Path configFolder = Paths.get(System.getProperty("user.dir"), "config", scope.toString(), identifierFolder);
            File folder = configFolder.toFile();
            folder.mkdirs();
            File file = Paths.get(System.getProperty("user.dir"), "config", scope.toString(), identifierFolder, String.format("%s.ini", name)).toFile();

            if (file.exists()) // Hack to ensure configs can overwrite correctly
                file.delete();

            file.createNewFile();
            Wini iniFile = new Wini(file);

            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                ConfigProperty config = field.getAnnotation(ConfigProperty.class);
                boolean shouldSerialze = field.getAnnotation(DontSerialize.class) == null;

                if (!shouldSerialze)
                    continue;

                if (config != null) {
                    iniFile.put(
                            config.section(),
                            config.name().isEmpty() ? field.getName() : config.name(),
                            field.get(object)
                    );
                } else {
                    iniFile.put(
                            "_",
                            field.getName(),
                            field.get(object)
                    );
                }
            }

            iniFile.store();

            //System.out.printf("Config data written to '%s'\n", file.getAbsolutePath());

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void loadConfig(Object object) {
        Config meta = object.getClass().getAnnotation(Config.class);
        SectionResolver resolver = new SectionResolver();

        if (meta == null) {
            logger.logVerbose("object does not declare metadata for config");
            return;
        }

        Wini finalIni = new Wini();

        for (int i = 0; i < ConfigScope.Count.ordinal(); i++) {
            Wini ini = loadIni(meta.name(), ConfigScope.values()[i]);

            if (ini != null) {
                Set<Map.Entry<String, Profile.Section>> entries = ini.entrySet();

                for (Map.Entry<String, Profile.Section> entry : entries) {
                    if (finalIni.containsKey(entry.getKey()))
                        finalIni.merge(entry.getKey(), entry.getValue(), resolver);
                    else
                        finalIni.put(entry.getKey(), entry.getValue());
                }
            }
        }

        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            ConfigProperty config = field.getAnnotation(ConfigProperty.class);
            boolean shouldSerialze = field.getAnnotation(DontSerialize.class) == null;

            if (!shouldSerialze)
                continue;

            Object val;
            if (config != null) {
                val = finalIni.get(
                        config.section(),
                        config.name().isEmpty() ? field.getName() : config.name(),
                        field.getType()
                );
            } else {
                val = finalIni.get(
                        "_",
                        field.getName(),
                        field.getType()
                );
            }

            try {
                if (val != null)
                    field.set(object, val); //TODO: Check that the null value is because nothing was found, and not because it was explicitly set
            } catch (Exception e) {
                logger.logError("object does not declare metadata for config");
                logger.logError(e.getMessage());
            }
        }
    }

    private static Wini loadIni(String name, ConfigScope scope) {
        Wini ini = new Wini();

        String uid = "";
        if (userIdentifier != null) {
            uid = userIdentifier.toString();
        }

        try {
            String identifierFolder = scope == ConfigScope.User ? uid : "";
            File file = Paths.get(System.getProperty("user.dir"), "config", scope.toString(), identifierFolder, String.format("%s.ini", name)).toFile();

            if (!file.exists()) {
                //System.out.println("INI file does not exist");

                return null;
            }

            ini.load(file);

            return ini;
        } catch (Exception e) {
            logger.logError(e.getMessage());
            return null;
        }
    }

    private static class SectionResolver implements BiFunction<Profile.Section, Profile.Section, Profile.Section> {
        //section2 is always the newer config one
        @Override
        public Profile.Section apply(Profile.Section section, Profile.Section section2) {
            //If section2 contains a value from section, override it
            for (String optionKey : section2.keySet()) {
                section.replace(optionKey, section2.get(optionKey));
            }

            return section;
        }
    }
}
