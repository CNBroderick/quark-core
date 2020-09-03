package org.bklab.quark.entity.converter.entity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class StringToJsonConverter implements StringToEntityConverter<JsonObject> {
    @Override
    public JsonObject apply(String s) {
        return new Gson().fromJson(s, JsonObject.class);
    }
}
