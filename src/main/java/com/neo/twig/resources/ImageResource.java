package com.neo.twig.resources;

import com.neo.twig.Engine;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class ImageResource extends Resource<Image> {
    protected ImageResource(Object json) {
        super(json);
    }

    @Override
    protected Image decodeResource(Object jsonObject) {
        JSONObject resource = (JSONObject) jsonObject;

        Path resourcePath = ResourcePath.resolveAssetPath(resource.get("path").toString());

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
                        resourcePath.toFile().toURL().openStream(),
                        //loader.getResource((String) resource.get("resource")).openStream(),
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
                //return new Image(loader.getResource((String) resource.get("resource")).openStream());
                return new Image(resourcePath.toFile().toURL().openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
