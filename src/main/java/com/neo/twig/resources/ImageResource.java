package com.neo.twig.resources;

import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@SuppressWarnings("unused")
public class ImageResource extends Resource<Image> {
    protected ImageResource(Object json) {
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

        URL resourceURL = loader.getResource((String) resource.get("resource"));
        try {
            setReloadPath(resourceURL.toURI());
        } catch (URISyntaxException ignored) {

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
                        resourceURL.openStream(),
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
                return new Image(resourceURL.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
