/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-04-20 12:07:17
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.mysql.MySqlWhereConditionBuilder
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.mysql;

import dataq.core.operation.OperationContext;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MySqlWhereConditionBuilder implements Supplier<String> {

    private final StringBuilder b = new StringBuilder(" (1=1)");
    private final OperationContext context;

    public MySqlWhereConditionBuilder(OperationContext context) {
        this.context = context;
    }

    public MySqlWhereConditionBuilder addAll(String... parameterNames) {
        Arrays.stream(parameterNames).forEach(this::add);
        return this;
    }

    /**
     * @param filedParameterMap filedName, parameterName
     * @return this
     */
    public MySqlWhereConditionBuilder addAll(Map<String, String> filedParameterMap) {
        filedParameterMap.forEach(this::add);
        return this;
    }

    public MySqlWhereConditionBuilder add(String parameterName) {
        StringBuilder b = new StringBuilder("d_");
        for (char c : parameterName.toCharArray()) {
            if (c >= 65 && c <= 90) {
                b.append('_').append((char) (c + 32));
            } else {
                b.append(c);
            }
        }
        return add(b.toString(), parameterName);
    }

    public MySqlWhereConditionBuilder add(String filedName, String parameterName) {
        Object object = context.getObject(parameterName);
        if (object == null) return this;

        if (object instanceof String) {
            b.append(" AND `").append(filedName).append("` = '").append(object).append("'");
        }

        if (object.getClass().isEnum()) {
            b.append(" AND `").append(filedName).append("` ").append('=').append(" '")
                    .append(((Enum<?>) object).name())
                    .append("'");
            return this;
        }

        if (object instanceof Collection<?>) {
            b.append(" AND `").append(filedName).append("` IN ('")
                    .append(((Collection<?>) object).stream().map(String::valueOf).distinct().collect(Collectors.joining("','")))
                    .append("')");
            return this;
        }

        accessParameter(filedName, parameterName);

        if (Stream.of(
                LocalDateTime.class,
                LocalDate.class,
                LocalTime.class,
                Date.class,
                Number.class
        ).noneMatch(object.getClass()::isInstance)) {
            b.append(" AND `").append(filedName).append("` = '").append(object).append("'");
        }

        return this;
    }

    private void accessCollection(String filedName, String parameterName) {
        Consumer<Collection<?>> consumer = collection -> b.append(" AND `").append(filedName).append("` IN ('")
                .append(collection.stream().map(this::serialize).distinct().collect(Collectors.joining("','")))
                .append("')");

        Object object = context.getObject(parameterName);
        if (object instanceof Collection) consumer.accept((Collection<?>) object);
        if (parameterName.length() == 1) return;

        Object o = context.getObject(InflectWord.getInstance().pluralize(parameterName));
            if (o instanceof Collection<?>) consumer.accept((Collection<?>) o);

    }

    private void accessParameter(String filedName, String parameterName) {
        if (parameterName.isEmpty()) return;
        accessParameter(filedName, parameterName, "=");

        String head;
        String tail = "";

        if (parameterName.length() == 1) {
            head = parameterName.toUpperCase();
        } else {
            head = parameterName.substring(0, 1).toUpperCase();
            tail = parameterName.substring(1);
        }

        accessParameter(filedName, "min" + head + tail, ">=");
        accessParameter(filedName, "max" + head + tail, "<=");
    }

    private void accessParameter(String filedName, String parameterName, String operation) {
        Object object = context.getObject(parameterName);
        if (object == null) return;

        if (object instanceof LocalDateTime) {
            b.append(" AND `").append(filedName).append("` ").append(operation).append(" '")
                    .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((TemporalAccessor) object))
                    .append("'");
            return;
        }

        if (object instanceof LocalDate) {
            b.append(" AND `").append(filedName).append("`").append(operation).append("'")
                    .append(DateTimeFormatter.ofPattern("yyyy-MM-dd").format((TemporalAccessor) object))
                    .append("'");
            return;
        }

        if (object instanceof LocalTime) {
            b.append(" AND `").append(filedName).append("`").append(operation).append("'")
                    .append(DateTimeFormatter.ofPattern("HH:mm:ss").format((TemporalAccessor) object))
                    .append("'");
            return;
        }

        if (object instanceof Date) {
            b.append(" AND `").append(filedName).append("`").append(operation).append("'")
                    .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object))
                    .append("'");
            return;
        }

        if (object instanceof Number) {
            b.append(" AND `").append(filedName).append("` ").append(operation).append(" ")
                    .append(object)
                    .append(" ");
        }

    }

    private String serialize(Object object) {
        if (object instanceof LocalDateTime) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((TemporalAccessor) object);
        }

        if (object instanceof LocalDate) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd").format((TemporalAccessor) object);
        }

        if (object instanceof LocalTime) {
            return DateTimeFormatter.ofPattern("HH:mm:ss").format((TemporalAccessor) object);
        }

        if (object instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object);
        }
        return String.valueOf(object);
    }

    public MySqlWhereConditionBuilder addCondition(String condition) {
        b.append(" ").append(condition);
        return this;
    }

    @Override
    public String get() {
        return Objects.toString(b.toString(), "")
                .replaceAll("\n", " ")
                .replaceAll("\t", " ")
                .replaceAll(" +", " ");
    }

    @Override
    public String toString() {
        return get();
    }

}
