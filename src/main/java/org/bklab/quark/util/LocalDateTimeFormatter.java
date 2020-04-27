/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-04-02 16:43:37
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.LocalDateTimeFormatter
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util;

import org.bklab.quark.util.function.LocalDateSupplier;
import org.bklab.quark.util.function.LocalDateTimeSupplier;
import org.bklab.quark.util.function.LocalTimeSupplier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Supplier;

public class LocalDateTimeFormatter {

    public static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter SHORT_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter CHINESE = DateTimeFormatter.ofPattern("yyyy年 MM月 dd日 HH时 mm分 ss秒");
    public static final DateTimeFormatter CHINESE_DATE = DateTimeFormatter.ofPattern("yyyy年 MM月 dd日");
    public static final DateTimeFormatter CHINESE_TIME = DateTimeFormatter.ofPattern("HH时 mm分 ss秒");

    public static String Short(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(SHORT::format).orElse(null);
    }

    public static String Short(LocalTime localTime) {
        return Optional.ofNullable(localTime).map(SHORT_TIME::format).orElse(null);
    }

    public static String Short(LocalDate localDate) {
        return Optional.ofNullable(localDate).map(SHORT_DATE::format).orElse(null);
    }

    public static <T> String Short(LocalDateTimeSupplier localDateTimeSupplier) {
        return Optional.ofNullable(localDateTimeSupplier)
                .map(Supplier::get).map(SHORT::format).orElse(null);
    }

    public static <T> String Short(LocalDateSupplier localDateSupplier) {
        return Optional.ofNullable(localDateSupplier)
                .map(Supplier::get).map(SHORT::format).orElse(null);
    }


    public static <T> String Short(LocalTimeSupplier localTimeSupplier) {
        return Optional.ofNullable(localTimeSupplier)
                .map(Supplier::get).map(SHORT_TIME::format).orElse(null);
    }

    public static String Chinese(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(CHINESE::format).orElse(null);
    }

    public static String Chinese(LocalDate localDate) {
        return Optional.ofNullable(localDate).map(CHINESE_DATE::format).orElse(null);
    }

    public static String Chinese(LocalTime localTime) {
        return Optional.ofNullable(localTime).map(CHINESE_TIME::format).orElse(null);
    }

    public static <T> String Chinese(LocalDateTimeSupplier localDateTimeSupplier) {
        return Optional.ofNullable(localDateTimeSupplier)
                .map(Supplier::get).map(CHINESE::format).orElse(null);
    }

    public static <T> String Chinese(LocalDateSupplier localDateSupplier) {
        return Optional.ofNullable(localDateSupplier)
                .map(Supplier::get).map(CHINESE_DATE::format).orElse(null);
    }


    public static <T> String Chinese(LocalTimeSupplier localTimeSupplier) {
        return Optional.ofNullable(localTimeSupplier)
                .map(Supplier::get).map(CHINESE_TIME::format).orElse(null);
    }
}
