package com.neo.twig.resources;

import org.json.simple.JSONObject;

import java.net.URL;

@SuppressWarnings("unused")
public class URLResource extends Resource<URL> {
    protected URLResource(Object json) {
        super(json);
    }

    @Override
    protected URL decodeResource(Object jsonObject) {
        JSONObject resource = (JSONObject) jsonObject;
        Class<?> loader;

        try {
            loader = Class.forName((String) resource.get("class"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return loader.getResource((String) resource.get("resource"));
    }
}
