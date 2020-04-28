package org.bklab.quark.entity.converter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.bklab.quark.entity.StringToEntityConverter;

public class StringToJsonArrayConverter implements StringToEntityConverter<JsonArray> {
    @Override
    public JsonArray apply(String s) {
        return new Gson().fromJson(s, JsonArray.class);
    }
}
