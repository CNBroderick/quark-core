/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-03-25 13:23:20
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.search.common.KeyWordSearcher
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.search.common;

import org.bklab.util.LocalDateTools;
import org.bklab.util.StringExtractor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class KeyWordSearcher<T> {

    private final T entity;

    public KeyWordSearcher(T entity) {
        this.entity = entity;
    }

    public boolean matchJava5(String keyword) {
        if (keyword == null || entity == null) return true;
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object o = field.get(entity);
                if (o != null && String.valueOf(o).toLowerCase().contains(keyword.toLowerCase()))
                    return true;
            } catch (IllegalAccessException ignore) {
            }
        }
        return false;
    }

    /**
     * 二级深度搜索
     */
    public boolean match(String keyword) {
        if (keyword == null || entity == null) return true;
        if (entity instanceof Record) return matchKeyword((Record) entity, keyword);
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .anyMatch(field -> {
                    try {
                        Object o = field.get(entity);
                        if (o == null) return false;
                        SkipSearch skipSearch = field.getAnnotation(SkipSearch.class);
                        if (skipSearch != null && skipSearch.skip()) {
                            return false;
                        }
                        if (o instanceof String)
                            return String.valueOf(o).toLowerCase().contains(keyword.toLowerCase());
                        if (o instanceof Number) {
                            double number = ((Number) o).doubleValue();
                            try {
                                if (number == Double.parseDouble(keyword)) {
                                    return true;
                                }
                            } catch (Exception e) {
                                Predicate<Double> ge = d -> number >= d;
                                Predicate<Double> g = d -> number > d;
                                Predicate<Double> eq = d -> number == d;
                                Predicate<Double> le = d -> number <= d;
                                Predicate<Double> l = d -> number < d;
                                if (keyword.startsWith(">=") && ge.test(StringExtractor.parseDouble(keyword)))
                                    return true;
                                if (keyword.startsWith(">") && g.test(StringExtractor.parseDouble(keyword)))
                                    return true;
                                if (keyword.startsWith("=") && eq.test(StringExtractor.parseDouble(keyword)))
                                    return true;
                                if (keyword.startsWith("<=") && le.test(StringExtractor.parseDouble(keyword)))
                                    return true;
                                if (keyword.startsWith("<") && l.test(StringExtractor.parseDouble(keyword)))
                                    return true;
                            }
                            return false;
                        }
                        if (o instanceof Collection || o.getClass().isEnum())
                            return o.toString().toLowerCase().contains(keyword.toLowerCase());
                        if (o instanceof LocalDate) {
                            return LocalDateTools.toChineseString((LocalDate) o).contains(keyword)
                                    || LocalDateTools.toShortString((LocalDate) o).contains(keyword);
                        }
                        if (o instanceof LocalDateTime) {
                            return DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").format((LocalDateTime) o).contains(keyword)
                                    || DateTimeFormatter.ofPattern("uuuu 年 MM月 dd日 HH时 mm分 ss秒").format((LocalDateTime) o).contains(keyword);
                        }
                        if (o instanceof Record) return matchKeyword((Record) o, keyword);
                        return new KeyWordSearcher<>(o).matchDirectly(keyword);
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                });
    }

    public boolean matchKeyword(Record r, String keyword) {
        return IntStream.range(0, r.getSchema().size()).mapToObj(r::getObject)
                .anyMatch(a -> Objects.toString(a, "").contains(keyword));
    }

    /**
     * 一级快速搜索
     */
    public boolean matchDirectly(String keyword) {
        if (keyword == null || entity == null) return true;
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .anyMatch(field -> {
                    try {
                        Object o = field.get(entity);
                        return o != null && String.valueOf(o).toLowerCase().contains(keyword.toLowerCase());
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                });
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SkipSearch {
        boolean skip() default true;
    }
}
