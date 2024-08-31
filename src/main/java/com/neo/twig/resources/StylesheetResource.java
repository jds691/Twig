package com.neo.twig.resources;

import com.neo.twig.Engine;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.nio.file.Path;

public class StylesheetResource extends Resource<String> {
    protected StylesheetResource(Object json) {
        super(json);
    }

    @Override
    protected String decodeResource(Object jsonObject) {
        JSONObject resource = (JSONObject) jsonObject;

        Path resourcePath = ResourcePath.resolveAssetPath(resource.get("path").toString());

        try {
            return resourcePath.toFile().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
