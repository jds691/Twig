package com.neo.twig.resources;

import org.json.simple.JSONObject;

public class StylesheetResource extends Resource<String> {
    public StylesheetResource(Object json) {
        super(json);
    }

    @Override
    protected String decodeResource(Object jsonObject) {
        JSONObject resource = (JSONObject) jsonObject;
        Class<?> loader;

        try {
            loader = Class.forName((String) resource.get("class"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return loader.getResource((String) resource.get("resource")).toExternalForm();
    }
}
