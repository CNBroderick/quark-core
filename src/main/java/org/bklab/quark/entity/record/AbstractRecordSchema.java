package org.bklab.quark.entity.record;

import dataq.core.data.schema.DataType;
import dataq.core.data.schema.Field;
import dataq.core.data.schema.Recordset;
import dataq.core.data.schema.Schema;
import org.bklab.quark.util.json.GsonJsonObjectUtil;
import org.bklab.quark.util.schema.RecordFactory;
import org.bklab.quark.util.schema.SchemaFactory;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractRecordSchema extends Schema implements IJsonRecordConsumer {

    protected final Map<Class<?>, IRecordJsonResultConvertor<?>> convertorMap = new LinkedHashMap<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    {
        initIRecordStringToObjectConvertor();
    }

    public AbstractRecordSchema() {
    }

    public void filedCaption(String field, String caption) {
        getField(field).setCaption(caption);
    }

    public void fieldCaptions(String... keyCaptions) {
        if (keyCaptions.length % 2 > 0) throw new IllegalArgumentException("key captions 参数个数必须为偶数");
        for (int i = 0; i < keyCaptions.length; i += 2) {
            getField(keyCaptions[i]).setCaption(keyCaptions[i + 1]);
        }
    }

    public void fieldCaptions(SchemaField... fields) {
        for (SchemaField field : fields) {
            getField(field.name).setCaption(field.caption);
        }
    }

    public void addFields(SchemaField... fields) {
        Arrays.stream(fields).map(field ->
                new Field(field.name, field.dataType).caption(field.caption)
        ).forEach(this::addField);
    }

    @Override
    public void accept(GsonJsonObjectUtil json, RecordFactory record) {
        try {
            parseJsonResult(json, record, Arrays.stream(Class.forName(getClass().getName()).getDeclaredFields()).map(a -> {
                try {
                    a.setAccessible(true);
                    return (SchemaField) a.get(this);
                } catch (Exception ignore) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableList()).toArray(new SchemaField[]{}));
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("解析失败", e);
        }
    }

    public void parseJsonResult(GsonJsonObjectUtil json, RecordFactory record, SchemaField... fields) {
        for (SchemaField field : fields) {
            try {

                if (field.isType(String.class) || field.dataType == DataType.STRING) {
                    record.set(field.name, json.string(field.json));
                    continue;
                }

                if (field.isType(Number.class)) {
                    record.set(field.name, json.number(field.json));
                    continue;
                }

                if (field.isType(Boolean.class) || field.dataType == DataType.BOOLEAN) {
                    record.set(field.name, json.getAsBoolean(field.json));
                    continue;
                }

                IRecordJsonResultConvertor<?> convertor = convertorMap.values().stream().filter(a -> a.test(field)).findFirst().orElse(null);
                if (convertor != null) {
                    record.set(field.name, convertor.apply(field.json));
                    continue;
                }

            } catch (Exception e) {
                LoggerFactory.getLogger(getClass()).error(String.format("解析字段[%s]失败：\n%s\n", json.string(field.json),
                        new GsonJsonObjectUtil(field).get()), e);
                continue;
            }
            throw new IllegalArgumentException("不支持的类型：" + field.dataType.name() + " 所属类：" + field.type.getName());
        }
    }

    protected LocalDateTime getDateTime(String content) {
        try {
            return LocalDateTime.parse(content, dateTimeFormatter);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("解析时间失败：" + content);
            return null;
        }
    }

    public SchemaFactory asFactory() {
        return new SchemaFactory(this);
    }

    public Recordset recordset() {
        return new Recordset(this);
    }

    protected void initIRecordStringToObjectConvertor() {
        IRecordJsonResultConvertor.getDefaults().forEach(this::add);
    }

    protected <T> AbstractRecordSchema add(IRecordJsonResultConvertor<T> convertor) {
        convertorMap.put(convertor.getClass(), convertor);
        return this;
    }

    protected Map<Class<?>, IRecordJsonResultConvertor<?>> getConvertorMap() {
        return convertorMap;
    }
}
