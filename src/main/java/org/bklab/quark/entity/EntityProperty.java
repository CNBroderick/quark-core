package org.bklab.quark.entity;

import java.util.*;

@SuppressWarnings("UnusedReturnValue")
public class EntityProperty<T> {

    /**
     * 其他属性
     */
    private final Map<String, String> properties = new LinkedHashMap<>();
    private final String name;
    /**
     * 所属类型
     */
    private final Class<? extends T> type;
    /**
     * 属性的别名（例如中文名称）
     */
    private String caption;
    /**
     * 数据库字段名称
     */
    private String fieldName;
    private T value;
    private String defaultValue;
    private boolean primary = false;
    private boolean required = false;
    private boolean readonly = false;
    private boolean autoIncrease = false;
    private List<String> fixValues = new ArrayList<>();

    /**
     * string 类型转换成实体类的转换器
     */
    private StringToEntityConverter<T> entityConverter = null;

    public EntityProperty(String name, Class<? extends T> type) {
        if (name == null || name.trim().length() < 1) throw new RuntimeException("name 不能为null或空字符串");
        this.name = name;
        this.type = type;
        this.fieldName = generateFieldName(name);
    }

    private String generateFieldName(String name) {
        StringBuilder b = new StringBuilder("d");
        char[] chars = name.toCharArray();
        if (chars[0] <= 'z' && chars[0] >= 'a') {
            chars[0] -= 32;
        }
        for (char c : chars) {
            if (c <= 'Z' && c >= 'A') {
                b.append('_');
                c += 32;
            }
            b.append(c);
        }
        return b.toString();
    }

    public EntityProperty<T> addProperty(String name, String value) {
        properties.put(name, value);
        return this;
    }

    public EntityProperty<T> addFixValue(String value) {
        fixValues.add(value);
        return this;
    }

    public T value(StringToEntityConverter<T> function) {
        T b = Optional.ofNullable(value).orElse(defaultValue == null ? null : function.apply(defaultValue));
        System.out.println("value = " + b);
        return b;
    }

    public T value() {
        return entityConverter == null ? value : value(entityConverter);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public EntityProperty<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public T getDefaultValue() {
        return entityConverter == null ? null : getDefaultValue(entityConverter);
    }

    public EntityProperty<T> setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public T getDefaultValue(StringToEntityConverter<T> function) {
        return defaultValue == null ? null : function.apply(defaultValue);
    }

    public boolean isPrimary() {
        return primary;
    }

    public EntityProperty<T> setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public EntityProperty<T> setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public EntityProperty<T> setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getCaption() {
        return caption;
    }

    public EntityProperty<T> setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public boolean isAutoIncrease() {
        return autoIncrease;
    }

    public EntityProperty<T> setAutoIncrease(boolean autoIncrease) {
        this.autoIncrease = autoIncrease;
        return this;
    }

    public List<String> getFixValues() {
        return fixValues;
    }

    public EntityProperty<T> setFixValues(List<String> fixValues) {
        this.fixValues = fixValues;
        return this;
    }

    public StringToEntityConverter<T> getEntityConverter() {
        return entityConverter;
    }

    public EntityProperty<T> setEntityConverter(StringToEntityConverter<?> stringToEntityConverter) {
        if (stringToEntityConverter != null)
            //noinspection unchecked
            this.entityConverter = (StringToEntityConverter<T>) stringToEntityConverter;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public EntityProperty<T> setFieldName(String fieldName) {
        this.fieldName = fieldName == null || fieldName.trim().isEmpty() ? generateFieldName(name) : fieldName;
        return this;
    }

    public EntityProperty<T> copy() {
        EntityProperty<T> property = new EntityProperty<>(name, type);
        property.properties.putAll(properties);
        property.caption = caption;
        property.fieldName = fieldName;
        property.value = value;
        property.defaultValue = defaultValue;
        property.primary = primary;
        property.required = required;
        property.readonly = readonly;
        property.autoIncrease = autoIncrease;
        property.fixValues = fixValues;
        property.entityConverter = entityConverter;

        return property;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EntityProperty.class.getSimpleName() + "{", "\n}")
                .add("\n\tproperties: " + properties)
                .add("\n\tname: '" + name + "'")
                .add("\n\ttype: " + type)
                .add("\n\tcaption: '" + caption + "'")
                .add("\n\tfieldName: '" + fieldName + "'")
                .add("\n\tvalue: " + value)
                .add("\n\tdefaultValue: '" + defaultValue + "'")
                .add("\n\tprimary: " + primary)
                .add("\n\trequired: " + required)
                .add("\n\treadonly: " + readonly)
                .add("\n\tautoIncrease: " + autoIncrease)
                .add("\n\tfixValues: " + fixValues)
                .add("\n\tentityConverter: " + entityConverter)
                .toString();
    }
}
