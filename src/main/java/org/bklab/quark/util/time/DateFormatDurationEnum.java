package org.bklab.quark.util.time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public enum DateFormatDurationEnum {
    YEAR(FormatDuration.createYear()),
    MONTH(FormatDuration.createMonth()),
    DAY(FormatDuration.createDay()),
    HOUR(FormatDuration.createHour()),
    MINUTE(FormatDuration.createMinute()),
    SECOND(FormatDuration.createSecond()),
    ;

    private final FormatDuration formatDuration;

    DateFormatDurationEnum(FormatDuration formatDuration) {
        this.formatDuration = formatDuration;
    }

    public static DateFormatDurationEnum create(Temporal start, Temporal end) {
        return DateFormatDurationEnum.create(Duration.between(start, end));
    }

    public static DateFormatDurationEnum create(Duration duration) {
        return DateFormatDurationEnum.create(duration.getSeconds());
    }

    public static DateFormatDurationEnum create(long seconds) {

        if (seconds > 60 * 60 * 24 * 367) {
            return YEAR;
        } else if (seconds > 60 * 60 * 24 * 32) {
            return MONTH;
        } else if (seconds > 60 * 60 * 25) {
            return DAY;
        } else if (seconds > 60 * 60 * 2) {
            return HOUR;
        } else if (seconds > 60 * 2) {
            return MINUTE;
        } else {
            return SECOND;
        }
    }

    public String pattern() {
        return formatDuration.pattern;
    }

    public String pattern(boolean isUseImpala) {
        return isUseImpala ? formatDuration.impalaPattern : formatDuration.pattern;
    }

    public String impalaPattern() {
        return formatDuration.impalaPattern;
    }

    public <T> Map<String, T> createMap(LocalDateTime start, LocalDateTime end, Function<String, T> function) {
        if (end == null) end = LocalDateTime.now();
        Map<String, T> map = new LinkedHashMap<>();
        LocalDateTime index = start;
        while (end.isAfter(index)) {
            String format = formatDuration.dbFormatter.format(index);
            map.put(formatDuration.styleFormatter.format(index), function.apply(format));
            index = formatDuration.next.apply(index);
        }
        return map;
    }

    private static class FormatDuration {
        private final String pattern;
        private final String impalaPattern;
        private final DateTimeFormatter dbFormatter;
        private final DateTimeFormatter styleFormatter;
        private final Function<LocalDateTime, LocalDateTime> next;


        public FormatDuration(String pattern, String dbPattern, Function<LocalDateTime, LocalDateTime> next) {
            this(pattern, dbPattern, dbPattern, next);
        }

        public FormatDuration(String pattern, String dbPattern, String stylePattern, Function<LocalDateTime, LocalDateTime> next) {
            this.pattern = pattern;
            this.impalaPattern = '\'' + dbPattern + '\'';
            this.dbFormatter = DateTimeFormatter.ofPattern(dbPattern);
            this.styleFormatter = DateTimeFormatter.ofPattern(stylePattern);
            this.next = next;
        }

        public static FormatDuration createYear() {
            return new FormatDuration("'%Y年'", "yyyy年", t -> t.plusYears(1));
        }

        public static FormatDuration createMonth() {
            return new FormatDuration("'%Y年%m月'", "yyyy年MM月", t -> t.plusMonths(1));
        }

        public static FormatDuration createDay() {
            return new FormatDuration("'%Y-%m-%d'", "yyyy-MM-dd", t -> t.plusDays(1));
        }

        public static FormatDuration createHour() {
            return new FormatDuration("'%Y-%m-%d %H:00'", "yyyy-MM-dd HH:mm", "HH:mm", t -> t.plusHours(1));
        }

        public static FormatDuration createMinute() {
            return new FormatDuration("'%Y-%m-%d %H:%i'", "yyyy-MM-dd HH:mm", "HH:mm", t -> t.plusMinutes(1));
        }

        public static FormatDuration createSecond() {
            return new FormatDuration("'%Y-%m-%d %H:%i:%s'", "yyyy-MM-dd HH:mm:ss", "HH:mm:ss", t -> t.plusSeconds(1));
        }
    }
}