package com.neo.twig.resources;

import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.io.IOException;

@SuppressWarnings("unused")
public class ImageResource extends Resource<Image> {
    public ImageResource(Object json) {
        super(json);
    }

    @Override
    protected Image decodeResource(Object jsonObject) {
        JSONObject resource = (JSONObject) jsonObject;
        Class<?> loader;

        try {
            loader = Class.forName((String) resource.get("class"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (resource.containsKey("requestedWidth") &&
                resource.containsKey("requestedHeight")) {
            double requestedWidth = Double.parseDouble(resource.get("requestedWidth").toString());
            double requestedHeight = Double.parseDouble(resource.get("requestedHeight").toString());
            boolean preserveAspectRatio = true;

            if (resource.containsKey("preserveAspectRatio")) {
                preserveAspectRatio = Boolean.parseBoolean(resource.get("preserveAspectRatio").toString());
            }

            try {
                return new Image(
                        loader.getResource((String) resource.get("resource")).openStream(),
                        requestedWidth,
                        requestedHeight,
                        preserveAspectRatio,
                        false
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return new Image(loader.getResource((String) resource.get("resource")).openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
