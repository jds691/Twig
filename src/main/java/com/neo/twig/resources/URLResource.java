package com.neo.twig.resources;

import com.neo.twig.Engine;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class URLResource extends Resource<URL> {
    protected URLResource(Object json) {
        super(json);
    }

    @Override
    protected URL decodeResource(Object jsonObject) {
        JSONObject resource = (JSONObject) jsonObject;
        /*Class<?> loader;

        try {
            loader = Class.forName((String) resource.get("class"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        Path resourcePath = ResourcePath.resolveAssetPath(resource.get("path").toString());

        try {
            return resourcePath.toFile().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
