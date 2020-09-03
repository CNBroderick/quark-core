package org.bklab.quark.entity.converter.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class StringToJsonArrayConverter implements StringToEntityConverter<JsonArray> {
    @Override
    public JsonArray apply(String s) {
        return new Gson().fromJson(s, JsonArray.class);
    }
}
