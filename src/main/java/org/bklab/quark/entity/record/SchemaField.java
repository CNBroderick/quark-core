package org.bklab.quark.entity.record;

import com.google.gson.GsonBuilder;
import dataq.core.data.schema.DataType;

public class SchemaField {
    public final String name;
    public final String json;
    public final String caption;
    public final DataType dataType;
    public final Class<?> type;

    public SchemaField(String name, String caption) {
        this(name, name, caption);
    }

    public SchemaField(String name, String json, String caption) {
        this(name, json, caption, DataType.STRING);
    }

    public SchemaField(String name, String json, String caption, String dataType) {
        this(name, json, caption, DataType.parse(dataType));
    }

    public SchemaField(String name, String json, String caption, DataType dataType) {
        this.name = name;
        this.json = json;
        this.caption = caption;
        this.dataType = dataType;
        this.type = dataType.isNumber() ? Number.class : String.class;
    }

    public SchemaField(String name, String json, String caption, DataType dataType, Class<?> type) {
        this.name = name;
        this.json = json;
        this.caption = caption;
        this.dataType = dataType;
        this.type = type;
    }

    public boolean isType(Class<?> targetClass) {
        return type == targetClass || targetClass.isAssignableFrom(type);
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
