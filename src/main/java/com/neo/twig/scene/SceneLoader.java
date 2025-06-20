package com.neo.twig.scene;

import com.neo.twig.annotations.DontSerialize;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.logger.Logger;
import com.neo.twig.resources.Resource;
import com.neo.twig.resources.ResourcePath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.nio.file.Path;
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

            Path resourcePath = ResourcePath.resolveAssetPath(locationData.get("path").toString());

            FileReader file;
            try {
                file = new FileReader(resourcePath.toFile());
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
        String componentType = (String) json.get("__T__");
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
                Class<?> fieldType = field.getType();
                Object value = parseValue(fieldType, fieldValue.getClass(), fieldValue);

                try {
                    field.set(
                            component,
                            value
                    );
                } catch (IllegalAccessException e) {
                    logger.logFatal("Unable to parse field.");
                    throw new RuntimeException();
                }
            }

            field.setAccessible(isAccessible);
        }

        return component;
    }

    private static Object parseValue(Class<?> intendedType, Class<?> currentType, Object value) {
        try {
            /*
             * TODO: Allow fields to declare custom deserializers e.g.
             * Allow the Resource<T> class to take a string and load the correct URL
             */
            if (Resource.class.isAssignableFrom(intendedType)) {
                // For resources that only need a path, a string to the path can be passed instead of a whole JSON object
                if (currentType == String.class) {
                    JSONObject jsonValue = new JSONObject();
                    jsonValue.put("path", value);

                    Constructor<?> resourceConstructor = intendedType.getDeclaredConstructor(Object.class);
                    resourceConstructor.setAccessible(true);

                    return resourceConstructor.newInstance(jsonValue);
                } else {
                    Constructor<?> resourceConstructor = intendedType.getDeclaredConstructor(Object.class);
                    resourceConstructor.setAccessible(true);

                    return resourceConstructor.newInstance(value);
                }
            } else if (currentType == JSONArray.class && currentType != intendedType) { //You never know, some freak might be looking for a JSONArray
                JSONArray valueArray = (JSONArray) value;
                Class<?> fieldArrayType = intendedType.componentType();

                Object fieldArray = Array.newInstance(fieldArrayType, valueArray.size());

                for (int i = 0; i < valueArray.size(); i++) {
                    Object jsonValueObject = valueArray.get(i);

                    currentType = fieldArrayType;

                    if (jsonValueObject.getClass() == JSONArray.class)
                        currentType = JSONArray.class;
                    else if (jsonValueObject.getClass() == String.class)
                        currentType = String.class;

                    Array.set(fieldArray, i, parseValue(fieldArrayType, currentType, jsonValueObject));
                }

                return fieldArray;
            } else {
                return value;
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
