package com.neo.twig.scene;

import com.neo.twig.annotations.DontSerialize;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.logger.Logger;
import com.neo.twig.resources.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("unused")
final class SceneLoader {
    private static final Logger logger = Logger.getFor(SceneLoader.class);
    private static final JSONParser parser = new JSONParser();

    static Scene loadFrom(URL resource) {
        FileReader file;
        JSONArray root;
        Scene scene = new Scene();

        try {
            file = new FileReader(resource.getFile());
            root = (JSONArray) parser.parse(file);
        } catch (IOException e) {
            logger.logFatal("Failed to load Scene from resource. Ensure it exists. Returning empty scene...");
            return scene;
        } catch (ParseException e) {
            logger.logFatal(String.format("Failed parse Scene file at %s. Ensure it is in the correct format. Returning empty scene...", resource.getFile()));
            return scene;
        }

        logger.logInfo(String.format("Loading scene '%s'...", resource.getFile()));

        for (Object obj : root) {
            scene.root.add(parseNode((JSONObject) obj));
        }

        logger.logInfo("Scene load complete");

        return scene;
    }

    private static Node parseNode(JSONObject json) {
        return parseNode(json, false);
    }

    static Node parseNode(JSONObject json, boolean suppressLogs) {
        JSONObject locationData = (JSONObject) json.get("location");
        Node node = new Node();
        node.setName((String) json.get("name"));

        if (locationData != null) {
            //TODO: Add log back
            //logger.logInfo(String.format("Loading TwigControl from '%s'...", location));

            Class<?> loader;

            try {
                loader = Class.forName((String) locationData.get("class"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            URL resource = loader.getResource((String) locationData.get("resource"));

            FileReader file;
            try {
                file = new FileReader(resource.getFile());
                JSONObject nodeJSON = (JSONObject) parser.parse(file);
                node = parseNode(nodeJSON, true);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            //This does not return the node as the following code automatically serves as an override system
        }

        if (!suppressLogs)
            logger.logInfo(String.format("Creating Node '%S'...", node.getName()));

        JSONArray components = (JSONArray) json.get("components");
        if (components != null && !components.isEmpty()) {
            for (Object obj : components) {
                NodeComponent component = parseComponent((JSONObject) obj);

                if (component != null)
                    node.addComponent(component);
            }
        }

        if (!suppressLogs)
            logger.logInfo(String.format("Created Node '%S' with %d component(s)", node.getName(), node.getComponents().size()));

        return node;
    }

    private static NodeComponent parseComponent(JSONObject json) {
        String componentType = (String) json.get("type");
        Class<? extends NodeComponent> componentClass;

        try {
            componentClass = (Class<? extends NodeComponent>) Class.forName(componentType);
        } catch (ClassNotFoundException e) {
            logger.logError(String.format("Failed to load component '%s'. Ignoring component, expect errors. Ensure the components package is marked as open to Twig.", componentType));
            return null;
        }

        Constructor<?>[] constructors = componentClass.getDeclaredConstructors();
        if (constructors.length == 0) {
            logger.logError(String.format("Failed to find accessible constructor for component '%s'. Ignoring component, expect errors", componentType));
            return null;
        }

        Constructor<? extends NodeComponent> constructor = (Constructor<? extends NodeComponent>) constructors[0];
        NodeComponent component;
        try {
            component = constructor.newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            logger.logError(String.format("Failed to find instantiate component '%s'. Ignoring component, expect errors", componentType));
            e.printStackTrace();
            return null;
        }

        ArrayList<Field> fields = new ArrayList<>();
        for (Class<?> c = component.getClass(); c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }

        for (Field field : fields) {
            boolean isAccessible = false;

            if (!Modifier.isStatic(field.getModifiers()))
                isAccessible = field.canAccess(component);

            if (field.getAnnotation(DontSerialize.class) != null) {
                logger.logVerbose(String.format("Field '%s' on component '%s' has been explicitly marked with DontSerialize. Ignoring field...", field.getName(), componentClass.getName()));
                continue;
            } else if (!isAccessible && field.getAnnotation(ForceSerialize.class) == null) {
                logger.logVerbose(String.format("Field '%s' on component '%s' is not accessible to the SceneLoader. Ignoring field...", field.getName(), componentClass.getName()));
                continue;
            } else if (!isAccessible && field.getAnnotation(ForceSerialize.class) != null) {
                logger.logVerbose(String.format("Field '%s' on component '%s' has been forcefully serialized", field.getName(), componentClass.getName()));
                field.setAccessible(true);
            }

            Object fieldValue = json.get(field.getName());
            if (fieldValue != null) {
                try {
                    /*
                    TODO: Allow fields to declare custom deserializers e.g.
                     Allow the Resource<T> class to take a string and load the correct URL
                      */
                    Class<?> fieldType = field.getType();

                    if (fieldType.getSuperclass() == Resource.class) {
                        Constructor<?> resourceConstructor = fieldType.getDeclaredConstructor(Object.class);
                        resourceConstructor.setAccessible(true);

                        field.set(
                                component,
                                resourceConstructor.newInstance(fieldValue)
                        );
                    } else {
                        field.set(component, fieldValue);
                    }

                    field.setAccessible(isAccessible);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return component;
    }
}
