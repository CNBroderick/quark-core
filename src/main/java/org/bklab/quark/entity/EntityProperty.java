package org.bklab.quark.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataq.util.DateTime;
import org.bklab.quark.entity.converter.entity.StringToEntityConverter;
import org.bklab.quark.entity.converter.saver.DefaultSaveConverter;
import org.bklab.quark.entity.converter.saver.EntitySaveConverter;
import org.bklab.quark.util.StringExtractor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

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
    /**
     * 数据库字段类型
     */
    private String fieldType;
    private T value;
    private String defaultValue;
    /**
     * 数据库默认值，如字符串请加单引号，例如： now()  15  'value'
     */
    private String fieldDefault;
    private boolean primary = false;
    private boolean notnull = false;
    private boolean readonly = false;
    private boolean autoIncrease = false;
    private boolean unique = false;
    private List<String> fixValues = new ArrayList<>();

    /**
     * string 类型转换成实体类的转换器
     */
    private StringToEntityConverter<T> entityConverter = null;

    private EntitySaveConverter<T> saveConverter = null;

    public EntityProperty(String name, Class<? extends T> type) {
        if (name == null || name.trim().length() < 1) throw new RuntimeException("name 不能为null或空字符串");
        this.name = name;
        this.type = type;
        this.fieldName = generateFieldName(name);
    }

    public String validate() {
        String alias = caption == null ? name : name + "(" + caption + ")";
        if (notnull && value == null) {
            return "必填属性[" + alias + "]为空。";
        }

        if (fixValues.size() > 0) {
            if (entityConverter != null) {
                if (fixValues.stream().map(v -> entityConverter.apply(v)).noneMatch(t -> t.equals(value))) {
                    return "属性[" + alias + "]属于固定值，仅可使用以下值：" + String.join(", ", fixValues);
                }
            } else {
                if (CharSequence.class.isAssignableFrom(type) && fixValues.stream().noneMatch(v -> v.equals(value))) {
                    return "属性[" + alias + "]属于固定值，仅可使用以下值：" + String.join(", ", fixValues);
                }
            }
        }

        return null;
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
        return Optional.ofNullable(value).orElse(defaultValue == null ? null : function.apply(defaultValue));
    }

    public T value() {
        return entityConverter == null ? value : value(entityConverter);
    }

    public Object toSaveValue() {
        if (value == null) {
            // 建表时使用了 d_xxx default ${fieldDefault} 所以插入null时会替换成 ${fieldDefault}
            if (fieldDefault != null) return null;

            if (defaultValue != null) {
                return getSaveConverter().apply(entityConverter.apply(defaultValue));
            }
            return null;
        }
        return getSaveConverter().apply(value);
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

    public boolean isNotnull() {
        return notnull;
    }

    public EntityProperty<T> setNotnull(boolean notnull) {
        this.notnull = notnull;
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

    public String getFieldType() {
        if (fieldType == null) {
            if (CharSequence.class.isAssignableFrom(type)) {
                int max = StringExtractor.parseInt(getProperties().getOrDefault("max-length", "0"));
                if (max > 0) {
                    return "varchar(" + max + ")";
                }
                if (!fixValues.isEmpty()) {
                    return "varchar(" + (fixValues.stream().mapToInt(String::length).max().orElse(255) + 1) + ")";
                }
                return "varchar(256)";
            }

            if (type.isAssignableFrom(Boolean.class)) return "tinyint(1)";

            if (Number.class.isAssignableFrom(type)) {
                if (type.isAssignableFrom(Integer.class)) return "int(11)";
                if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(Float.class)) return "decimal(24, 4)";
                if (type.isAssignableFrom(Long.class)) {
                    String max = getProperties().getOrDefault("max", "");
                    int length = Math.max(max.length(), 20);
                    return "decimal(" + length + ", 0)";
                }
                return "decimal(24,4)";
            }

            if (Stream.of(LocalDateTime.class, DateTime.class, Date.class).anyMatch(type::isAssignableFrom))
                return "datetime";
            if (Stream.of(LocalDate.class, java.sql.Date.class).anyMatch(type::isAssignableFrom)) return "date";
            if (Stream.of(LocalTime.class, java.sql.Time.class).anyMatch(type::isAssignableFrom)) return "time";
            if (Stream.of(JsonObject.class, JsonArray.class).anyMatch(type::isAssignableFrom)) return "json";

        }
        return fieldType;
    }

    public EntityProperty<T> setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public EntitySaveConverter<T> getSaveConverter() {
        return saveConverter == null ? new DefaultSaveConverter<>() : saveConverter;
    }

    public EntityProperty<T> setSaveConverter(EntitySaveConverter<?> saveConverter) {
        //noinspection unchecked
        this.saveConverter = (saveConverter == null ? new DefaultSaveConverter<>() : (EntitySaveConverter<T>) saveConverter);
        return this;
    }

    public boolean isUnique() {
        return unique;
    }

    public EntityProperty<T> setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public String getFieldDefault() {
        return fieldDefault;
    }

    public EntityProperty<T> setFieldDefault(String fieldDefault) {
        this.fieldDefault = fieldDefault;
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
        property.notnull = notnull;
        property.readonly = readonly;
        property.autoIncrease = autoIncrease;
        property.fixValues = fixValues;
        property.entityConverter = entityConverter;
        property.saveConverter = saveConverter;
        property.fieldType = fieldType;
        property.unique = unique;
        property.fieldDefault = fieldDefault;

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
                .add("\n\tfieldType: '" + fieldType + "'")
                .add("\n\tvalue: " + value)
                .add("\n\tdefaultValue: '" + defaultValue + "'")
                .add("\n\tfieldDefault: '" + fieldDefault + "'")
                .add("\n\tprimary: " + primary)
                .add("\n\tnotnull: " + notnull)
                .add("\n\treadonly: " + readonly)
                .add("\n\tautoIncrease: " + autoIncrease)
                .add("\n\tunique: " + unique)
                .add("\n\tfixValues: " + fixValues)
                .add("\n\tentityConverter: " + entityConverter)
                .add("\n\tsaveConverter: " + saveConverter)
                .toString();
    }
}
