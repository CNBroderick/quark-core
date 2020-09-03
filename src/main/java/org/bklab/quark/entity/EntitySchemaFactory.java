package org.bklab.quark.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataq.core.xml.XmlObject;
import org.bklab.quark.entity.converter.entity.StringToEntityConverter;
import org.bklab.quark.entity.converter.saver.DefaultSaveConverter;
import org.bklab.quark.entity.converter.saver.EntitySaveConverter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class EntitySchemaFactory implements Function<String, EntitySchema> {

    private final Map<String, EntitySchema> map = new LinkedHashMap<>();

    public EntitySchemaFactory(XmlObject xmlObject) {
        parse(xmlObject);
    }

    private void parse(XmlObject xmlObject) {
        String tag = xmlObject.getTag();
        if (tag.equals("schema")) {
            add(parseSchema(xmlObject));
            return;
        }

        if (tag.equals("entities")) {
            for (XmlObject schema : xmlObject.children("schema")) {
                parse(schema);
            }
            return;
        }

        throw new XmlParseException(xmlObject, "未找到根节点", "entities", "schema");
    }

    private void add(EntitySchema entitySchema) {
        if (entitySchema == null) return;
        map.put(entitySchema.getName(), entitySchema);
    }

    private EntitySchema parseSchema(XmlObject xmlObject) {
        EntitySchema schema = new EntitySchema().setName(xmlObject.getString("name"));
        schema.setTableName(xmlObject.getString("table-name", generateTableName(schema.getName())));

        XmlObject properties = xmlObject.getFirstChild("properties");
        if (properties == null)
            throw new XmlParseException(xmlObject, "未定义 properties", "entities", "schema", "properties");

        if (schema.getName() == null || schema.getName().strip().isEmpty()) {
            throw new XmlParseException(xmlObject, "schema 标签 name 属性未定义", "entities", "schema", "name");
        }


        for (XmlObject property : properties.children("property")) {
            schema.addProperty(parseProperty(property));
        }

        return schema;
    }

    private String generateTableName(String name) {
        StringBuilder b = new StringBuilder("tb");
        char[] chars = name.toCharArray();

        if (chars.length == 0) return "";

        if (chars[0] >= 'a' && chars[0] <= 'z') {
            chars[0] = (char) (chars[0] - 32);
        }

        for (char c : chars) {
            if (c >= 'A' && c <= 'Z') {
                b.append('_').append((char) (c + 32));
                continue;
            }
            b.append(c);
        }
        return b.toString();
    }

    private EntityProperty<?> parseProperty(XmlObject xmlObject) {
        String name = xmlObject.getString("name");
        String type = xmlObject.getString("type", "string");

        if(name == null || name.strip().isEmpty() || name.equals("entityInstanceId"))
            throw new XmlParseException(xmlObject,"Property名称不能为空字符串或entityInstanceId", "entities", "schema","properties","property");

        Class<?> target = parseType(type);
        if (target == null) throw new XmlParseException(xmlObject, "未找到类型：" + type +
                "。请检查是否为基本类型[string,s,int,integer,i,long,l,double,d,float,f,char,c, json, json-array]" +
                "或类在包java.util, java.time, java.lang下", "entities", "schema", "properties", "property");

        EntityProperty<?> entityProperty = new EntityProperty<>(name, target)
                .setFieldType(xmlObject.getString("filed-type", null))
                .setPrimary(Boolean.parseBoolean(xmlObject.getString("primary", "false")))
                .setReadonly(Boolean.parseBoolean(xmlObject.getString("readonly", "false")))
                .setAutoIncrease(Boolean.parseBoolean(xmlObject.getString("auto-increase", "false")))
                .setNotnull(Boolean.parseBoolean(xmlObject.getString("notnull", "false")))
                .setUnique(Boolean.parseBoolean(xmlObject.getString("unique", "false")))
                .setDefaultValue(xmlObject.getString("default-value", null))
                .setFieldDefault(xmlObject.getString("field-default", null))
                .setCaption(xmlObject.getString("caption", null))
                ;

        Optional.ofNullable(xmlObject.getString("filed-name", null)).ifPresent(entityProperty::setFieldName);

        for (String s : xmlObject.keySet()) {
            entityProperty.addProperty(s, xmlObject.getString(s));
        }

        XmlObject values = xmlObject.getFirstChild("values");
        if (values != null) {
            for (XmlObject value : values.children("value")) {
                entityProperty.addFixValue(value.getContent());
            }
        }

        String entityConverter = xmlObject.getString("entity-converter", null);
        if (entityConverter != null) {
            try {
                Class<StringToEntityConverter<?>> converterClass = getEntityConverterClass(entityConverter);
                if (converterClass != null)
                    entityProperty.setEntityConverter(converterClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                throw new XmlParseException(xmlObject, "未找到 entity-converter" + entityConverter, "entities", "schema", "properties", "property");
            }
        } else {
            Optional.ofNullable(EntityConverterManager.getInstance().apply(target)).ifPresent(entityProperty::setEntityConverter);
        }

        String saveConverter = xmlObject.getString("save-converter", null);
        if (saveConverter != null) {
            try {
                Class<EntitySaveConverter<?>> saveConverterClass = getSaveConverterClass(saveConverter);
                if (saveConverterClass != null)
                    entityProperty.setSaveConverter(saveConverterClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                throw new XmlParseException(xmlObject, "未找到 save-converter" + entityConverter, "entities", "schema", "properties", "property");
            }
        } else {
            entityProperty.setSaveConverter(new DefaultSaveConverter<>());
        }

        String fieldType = entityProperty.getFieldType();
        if (fieldType == null)
            throw new XmlParseException(xmlObject, "无法根据类型自动生成数据库属性类型，请使用<property name = \"" + name + "\" type = \"" + type + "\" filed-type =? >属性手动指定。", "entities", "schema", "properties", "property", "field-type");
        entityProperty.setFieldType(fieldType);

        String caption = entityProperty.getCaption();
        if (caption != null && caption.contains("'")) {
            throw new XmlParseException(xmlObject, "property --> caption属性不能包含单引号。", "entities", "schema", "properties", "property", "caption");
        }

        if(entityProperty.isAutoIncrease() && !entityProperty.isPrimary()) {
            throw new XmlParseException(xmlObject, "property --> auto-increase 属性必须是 primary。", "entities", "schema", "properties", "property", "auto-increase");
        }

        if(entityProperty.isAutoIncrease() && entityProperty.getDefaultValue() != null) {
            throw new XmlParseException(xmlObject, "property --> auto-increase 不能有属性：default-value 与 field-default。", "entities", "schema", "properties", "property", "auto-increase");
        }

        return entityProperty;
    }

    private Class<StringToEntityConverter<?>> getEntityConverterClass(String name) {
        Class<?> c = null;
        try {
            c = Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                c = Class.forName(getClass().getPackageName() + ".converter.entity." + name);
            } catch (ClassNotFoundException ex) {
                e.printStackTrace();
                ex.printStackTrace();
            }
        }
        if (c != null) {
            //noinspection unchecked
            return (Class<StringToEntityConverter<?>>) c;
        }
        return null;
    }

    private Class<EntitySaveConverter<?>> getSaveConverterClass(String name) {
        Class<?> c = null;
        try {
            c = Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                c = Class.forName(getClass().getPackageName() + ".converter.saver." + name);
            } catch (ClassNotFoundException ex) {
                e.printStackTrace();
                ex.printStackTrace();
            }
        }
        if (c != null) {
            //noinspection unchecked
            return (Class<EntitySaveConverter<?>>) c.asSubclass(EntitySaveConverter.class);
        }
        return null;
    }

    private Class<?> parseType(String name) {
        if (List.of("string", "s").contains(name.toLowerCase())) return String.class;
        if (List.of("int", "integer", "i").contains(name.toLowerCase())) return Integer.class;
        if (List.of("long", "l").contains(name.toLowerCase())) return Long.class;
        if (List.of("double", "d").contains(name.toLowerCase())) return Double.class;
        if (List.of("float", "f").contains(name.toLowerCase())) return Float.class;
        if (List.of("char", "c").contains(name.toLowerCase())) return Character.class;
        if (name.equals("json")) return JsonObject.class;
        if (name.equals("json-array")) return JsonArray.class;

        name = name.strip();
        Class<?> target = parseClass(name);
        if (target != null) return target;

        target = parseClass("java.util." + name);
        if (target != null) return target;

        target = parseClass("java.time." + name);
        if (target != null) return target;

        target = parseClass("java.lang." + name);
        if (target != null) return target;

        target = parseClass("java." + name);

        return target;
    }

    private Class<?> parseClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public EntitySchema apply(String schemaName) {
        EntitySchema schema = map.get(schemaName);
        if (schema != null) return schema.copy();
        throw new NotFoundException("未找到名为[" + schemaName + "]的schema", "entities", "schema");
    }

    public EntitySchema createSchema(String schemaName) {
        return apply(schemaName);
    }

    public Entity createEntity(String schemaName) {
        return new Entity(apply(schemaName));
    }

    private static class XmlParseException extends RuntimeException {
        public XmlParseException(XmlObject xmlObject, String message, String... position) {
            super(message + "\n\t位置：" + String.join(" --> ", position) + "\n\t内容：" + xmlObject.toXML());
        }
    }

    private static class NotFoundException extends RuntimeException {

        public NotFoundException(String message, String... position) {
            super(message + "\n\t位置：" + String.join(" --> ", position));
        }
    }
}
