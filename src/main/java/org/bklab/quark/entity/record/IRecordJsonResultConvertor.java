package org.bklab.quark.entity.record;

import dataq.core.data.schema.DataType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface IRecordJsonResultConvertor<T> extends Function<String, T>, Predicate<SchemaField> {

    @Override
    boolean test(SchemaField schemaField);

    @Override
    T apply(String source);

    default String id() {
        return getClass().getName();
    }

    static List<IRecordJsonResultConvertor<?>> getDefaults() {
        return List.of(
                new IRecordJsonResultConvertor<>() {
                    @Override
                    public boolean test(SchemaField schemaField) {
                        return schemaField.isType(LocalDateTime.class);
                    }

                    @Override
                    public Object apply(String source) {
                        return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                },
                new IRecordJsonResultConvertor<>() {
                    @Override
                    public boolean test(SchemaField schemaField) {
                        return schemaField.isType(LocalDate.class);
                    }

                    @Override
                    public Object apply(String source) {
                        return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                },
                new IRecordJsonResultConvertor<>() {
                    @Override
                    public boolean test(SchemaField schemaField) {
                        return schemaField.isType(LocalTime.class);
                    }

                    @Override
                    public Object apply(String source) {
                        return LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss"));
                    }
                }
        );
    }
}
