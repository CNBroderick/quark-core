package org.bklab.quark.entity.converter.saver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DefaultSaveConverter<T> implements EntitySaveConverter<T> {

    @Override
    public Object apply(T value) {
        if(value == null) return null;
        if(value instanceof JsonObject) return value.toString();
        if(value instanceof JsonArray) return value.toString();
        if(value.getClass().isEnum()) return ((Enum<?>) value).name();
        if(value instanceof Number) return value.toString();
        if(value instanceof CharSequence) return value.toString();
        return value;
    }
}
