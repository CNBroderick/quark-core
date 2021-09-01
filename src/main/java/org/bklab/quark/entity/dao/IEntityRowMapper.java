/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-03-24 14:55:57
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.entity.IEntityRowMapper
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.entity.dao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * public interface IEntityRowMapper<T> {
 * T mapRow(ResultSet resultSet) throws Exception;
 * }
 */
public interface IEntityRowMapper<T> extends Function<ResultSet, T> {

    @Override
    default T apply(ResultSet resultSet) {
        try {
            return mapRow(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    T mapRow(ResultSet r) throws Exception;

    default LocalDateTime getLocalDateTime(ResultSet r, String filedName) throws Exception {
        if (r.getString(filedName) == null) return null;
        return LocalDateTime.of(r.getDate(filedName).toLocalDate(), r.getTime(filedName).toLocalTime());
    }

    default LocalDate getLocalDate(ResultSet r, String filedName) throws Exception {
        if (r.getString(filedName) == null) return null;
        return r.getDate(filedName).toLocalDate();
    }

    default LocalTime getLocalTime(ResultSet r, String filedName) throws Exception {
        if (r.getString(filedName) == null) return null;
        return r.getTime(filedName).toLocalTime();
    }

    default List<String> getJsonArrayString(ResultSet r, String filedName) throws Exception {
        return getJsonArrayElement(r, filedName).stream()
                .map(jsonElement -> jsonElement.isJsonPrimitive() ? jsonElement.getAsString() : jsonElement.toString())
                .collect(Collectors.toList());
    }

    default List<JsonElement> getJsonArrayElement(ResultSet r, String filedName) throws Exception {
        List<JsonElement> list = new ArrayList<>();
        for (JsonElement jsonElement : getJsonArray(r, filedName)) {
            list.add(jsonElement);
        }
        return list;
    }

    default JsonArray getJsonArray(ResultSet r, String filedName) throws Exception {
        return Optional.ofNullable(r.getString(filedName)).map(string -> new Gson().fromJson(string, JsonArray.class)).orElseGet(JsonArray::new);
    }

    default JsonObject getJsonObject(ResultSet r, String filedName) throws Exception {
        return Optional.ofNullable(r.getString(filedName)).map(string -> new Gson().fromJson(string, JsonObject.class)).orElseGet(JsonObject::new);
    }
}
