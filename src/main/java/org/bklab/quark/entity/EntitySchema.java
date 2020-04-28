package org.bklab.quark.entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class EntitySchema {

    private final Map<String, EntityProperty<?>> properties = new LinkedHashMap<>();
    private String name;
    private String tableName;

    {
        EntityProperty<Long> entityInstanceId = new EntityProperty<>("entityInstanceId", Long.class)
                .setDefaultValue("-1").setReadonly(true).setEntityConverter(EntityConverterManager.getInstance().apply(Long.class));
        properties.put("entityInstanceId", entityInstanceId);
    }

    public long getEntityInstanceId() {
        EntityProperty<Long> property = get("entityInstanceId");
         return property.value();
    }

    public int size() {
        return properties.size();
    }

    public <T> EntitySchema addProperty(EntityProperty<T> property) {
        this.properties.put(property.getName(), property);
        return this;
    }

    public <T> EntityProperty<T> get(String propertyName) {
        //noinspection unchecked
        return (EntityProperty<T>) properties.get(propertyName);
    }

    public <T> T getValue(String propertyName) {
        //noinspection unchecked
        return (T) get(propertyName).value();
    }

    public <T> EntitySchema setValue(String propertyName, T value) {
        get(propertyName).setValue(value);
        return this;
    }

    public Map<String, EntityProperty<?>> getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

    public EntitySchema setName(String name) {
        this.name = name;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public EntitySchema setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public EntitySchema copy() {
        EntitySchema schema = new EntitySchema();
        schema.name = name;
        schema.tableName = tableName;
        properties.forEach((k, v) -> schema.addProperty(v.copy()));
        return schema;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EntitySchema.class.getSimpleName() + "{", "\n}")
                .add("\n\tproperties: " + properties)
                .add("\n\tname: '" + name + "'")
                .add("\n\ttableName: '" + tableName + "'")
                .toString();
    }
}
