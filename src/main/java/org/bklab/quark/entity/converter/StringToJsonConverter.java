package org.bklab.quark.entity.converter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bklab.quark.entity.StringToEntityConverter;

public class StringToJsonConverter implements StringToEntityConverter<JsonObject> {
    @Override
    public JsonObject apply(String s) {
        return new Gson().fromJson(s, JsonObject.class);
    }
}
