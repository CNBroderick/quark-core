package org.bklab.quark.util.time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public enum DateFormatDurationEnum {
    YEAR("'%Y'", DateTimeFormatter.ofPattern("yyyy"), t -> t.plusYears(1)),
    MONTH("'%Y-%m'", DateTimeFormatter.ofPattern("yyyy-MM"), t -> t.plusMonths(1)),
    DAY("'%Y-%m-%d'", DateTimeFormatter.ofPattern("yyyy-MM-dd"), t -> t.plusDays(1)),
    HOUR("'%Y-%m-%d %H'", DateTimeFormatter.ofPattern("yyyy-MM-dd HH"), t -> t.plusHours(1)),
    MINUTE("'%Y-%m-%d %H:%i'", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"), t -> t.plusMonths(1)),
    SECOND("'%Y-%m-%d %H:%i:%s'", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), t -> t.plusSeconds(1)),
    ;

    public final String pattern;
    private final DateTimeFormatter formatter;
    private final Function<LocalDateTime, LocalDateTime> next;

    DateFormatDurationEnum(String pattern, DateTimeFormatter formatter, Function<LocalDateTime, LocalDateTime> next) {
        this.pattern = pattern;
        this.formatter = formatter;
        this.next = next;
    }

    public static DateFormatDurationEnum create(Temporal start, Temporal end) {
        return DateFormatDurationEnum.create(Duration.between(start, end));
    }

    public static DateFormatDurationEnum create(Duration duration) {
        return DateFormatDurationEnum.create(duration.getSeconds());
    }

    public static DateFormatDurationEnum create(long seconds) {

        if (seconds > 60 * 60 * 24 * 365) {
            return YEAR;
        } else if (seconds > 60 * 60 * 24 * 31) {
            return MONTH;
        } else if (seconds > 60 * 60 * 24) {
            return DAY;
        } else if (seconds > 60 * 60) {
            return HOUR;
        } else if (seconds > 60) {
            return MINUTE;
        } else {
            return SECOND;
        }
    }

    public <T> Map<String, T> createMap(LocalDateTime start, LocalDateTime end, Function<String, T> function) {
        if (end == null) end = LocalDateTime.now();
        Map<String, T> map = new LinkedHashMap<>();
        LocalDateTime index = start;
        while (end.isAfter(index)) {
            String format = formatter.format(index);
            map.put(format, function.apply(format));
            index = next.apply(index);
        }
        return map;
    }
}
