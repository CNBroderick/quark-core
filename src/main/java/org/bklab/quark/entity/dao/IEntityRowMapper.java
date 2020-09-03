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

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * public interface IEntityRowMapper<T> {
 * T mapRow(ResultSet resultSet) throws Exception;
 * }
 *
 * @see dataq.core.jdbc.IRowMapper
 */
public interface IEntityRowMapper<T> extends dataq.core.jdbc.IRowMapper {
    @Override
    T mapRow(ResultSet r) throws Exception;

    default LocalDateTime getLocalDateTime(ResultSet r, String fileName) throws Exception {
        if (r.getString(fileName) == null) return null;
        return LocalDateTime.of(r.getDate(fileName).toLocalDate(), r.getTime(fileName).toLocalTime());
    }

    default LocalDate getLocalDate(ResultSet r, String fileName) throws Exception {
        if (r.getString(fileName) == null) return null;
        return r.getDate(fileName).toLocalDate();
    }

    default LocalTime getLocalTime(ResultSet r, String fileName) throws Exception {
        if (r.getString(fileName) == null) return null;
        return r.getTime(fileName).toLocalTime();
    }
}
