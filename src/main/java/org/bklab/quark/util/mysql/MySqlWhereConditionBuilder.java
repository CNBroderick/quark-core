/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-04-10 10:41:58
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.MySqlWhereConditionBuilder
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
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MySqlWhereConditionBuilder implements Supplier<String> {

    private final StringBuilder b = new StringBuilder(" (1=1)");
    private final OperationContext context;
    private final BiConsumer<String, Collection<?>> collectionConsumer = (filedName, collection) ->
            b.append(" AND ").append(filedName).append(" IN ('")
                    .append(collection.stream().map(this::serialize).distinct().collect(Collectors.joining("','")))
                    .append("')");

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
        return add(convertToDbFormat(parameterName), parameterName);
    }

    private String convertToDbFormat(String parameterName) {
        StringBuilder b = new StringBuilder("d_");
        for (char c : parameterName.toCharArray()) {
            if (c >= 65 && c <= 90) {
                b.append('_').append((char) (c + 32));
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }

    public boolean hasParameter(String parameterName) {
        String head;
        String tail = "";

        if (parameterName.length() == 1) {
            head = parameterName.toUpperCase();
        } else {
            head = parameterName.substring(0, 1).toUpperCase();
            tail = parameterName.substring(1);
        }

        return context.getObject(parameterName) != null
                || context.getObject(InflectWord.getInstance().pluralize(parameterName)) != null
                || context.getObject("min" + head + tail) != null
                || context.getObject("max" + head + tail) != null
                ;
    }

    /**
     * 多个字段中的一个等于同一个值
     *
     * @param parameterName 参数名
     * @param filedNames    数据库字段
     * @return this;
     */
    public MySqlWhereConditionBuilder addOrCondition(String parameterName, String... filedNames) {
        if (filedNames == null || filedNames.length == 0) return this;
        if (!hasParameter(parameterName)) return this;

        b.append(" AND (");
        int i = 0;
        for (String filedName : filedNames) {
            add(i++ > 0 ? "OR" : "", filedName, parameterName);
        }
        b.append(")");

        return this;
    }

    /**
     * 或条件 AND ( condition 1 OR condition 2...)
     *
     * @param filedParameterMap key:数据库字段名称 value:参数名称
     * @return this
     */
    public MySqlWhereConditionBuilder addOrCondition(Map<String, String> filedParameterMap) {
        if (filedParameterMap == null) return this;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        filedParameterMap.forEach((key, value) -> {
            if (hasParameter(value)) map.put(key, value);
        });

        if (map.isEmpty()) return this;
        b.append(" AND (");
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            add(i++ > 0 ? "OR" : "", entry.getKey(), entry.getValue());
        }

        b.append(")");
        return this;
    }

    public MySqlWhereConditionBuilder add(String filedName, String parameterName) {
        return add("AND", filedName, parameterName);
    }

    public MySqlWhereConditionBuilder add(String modifier, String filedName, String parameterName) {
        Object object = context.getObject(InflectWord.getInstance().pluralize(parameterName));
        if (object instanceof Collection<?>) {
            collectionConsumer.accept(filedName, (Collection<?>) object);
        }

        object = context.getObject(parameterName);
        if (object == null) {
            return this;
        }

        if (object instanceof String) {
            b.append(" ").append(modifier).append(" ").append(filedName).append(" = '").append(object).append("'");
        }

        if (object.getClass().isEnum()) {
            b.append(" ").append(modifier).append(" ").append(filedName).append(" ").append('=').append(" '")
                    .append(((Enum<?>) object).name())
                    .append("'");
            return this;
        }

        if (object instanceof Collection<?>) {
            collectionConsumer.accept(filedName, (Collection<?>) object);
            return this;
        }

        if (accessParameter(modifier, filedName, parameterName)) return this;

        b.append(" ").append(modifier).append(" ").append(filedName).append(" = '").append(object).append("'");

        return this;
    }

    private boolean accessParameter(String modifier, String filedName, String parameterName) {
        if (parameterName.isEmpty()) return false;
        boolean flag = false;
        if (accessParameter(modifier, filedName, parameterName, "=")) flag = true;

        String head;
        String tail = "";

        if (parameterName.length() == 1) {
            head = parameterName.toUpperCase();
        } else {
            head = parameterName.substring(0, 1).toUpperCase();
            tail = parameterName.substring(1);
        }

        if (accessParameter(modifier, filedName, "min" + head + tail, ">=") && !flag) flag = true;
        if (accessParameter(modifier, filedName, "max" + head + tail, "<=") && !flag) flag = true;
        return flag;
    }

    private boolean accessParameter(String modifier, String filedName, String parameterName, String operation) {
        Object object = context.getObject(parameterName);
        if (object == null) return false;

        if (object instanceof LocalDateTime) {
            b.append(" ").append(modifier).append(" ").append(filedName).append(" ").append(operation).append(" '")
                    .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((TemporalAccessor) object))
                    .append("'");
            return true;
        }

        if (object instanceof LocalDate) {
            b.append(" ").append(modifier).append(" ").append(filedName).append(operation).append("'")
                    .append(DateTimeFormatter.ofPattern("yyyy-MM-dd").format((TemporalAccessor) object))
                    .append("'");
            return true;
        }

        if (object instanceof LocalTime) {
            b.append(" ").append(modifier).append(" ").append(filedName).append(operation).append("'")
                    .append(DateTimeFormatter.ofPattern("HH:mm:ss").format((TemporalAccessor) object))
                    .append("'");
            return true;
        }

        if (object instanceof Date) {
            b.append(" ").append(modifier).append(" ").append(filedName).append(operation).append("'")
                    .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object))
                    .append("'");
            return true;
        }

        if (object instanceof Number) {
            b.append(" ").append(modifier).append(" ").append(filedName).append(" ").append(operation).append(" ")
                    .append(object)
                    .append(" ");
            return true;
        }
        return false;
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

        if (object instanceof Enum<?>) {
            return ((Enum<?>) object).name();
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
                .replaceAll(" +", " ")
                .replaceAll("\n+", " ")
                .replaceAll("\t+", " ")
                ;
    }

    @Override
    public String toString() {
        return get();
    }

}
