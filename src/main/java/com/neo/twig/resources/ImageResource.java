package com.neo.twig.resources;

import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.io.IOException;

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

        try {
            return new Image(loader.getResource((String) resource.get("resource")).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
