package org.bklab.quark.util.time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess"})
public class LocalDateTools {
    public static LocalDate minLocalDate(LocalDate... localDates) {
        return Stream.of(localDates).filter(Objects::nonNull).min(Comparator.comparing(LocalDate::toEpochDay)).orElse(null);
    }

    public static LocalDate minLocalDate(List<LocalDate> localDates) {
        return localDates.stream().filter(Objects::nonNull).min(Comparator.comparing(LocalDate::toEpochDay)).orElse(null);
    }

    public static LocalDate maxLocalDate(LocalDate... localDates) {
        return Stream.of(localDates).filter(Objects::nonNull).max(Comparator.comparing(LocalDate::toEpochDay)).orElse(null);
    }

    public static LocalDate maxLocalDate(List<LocalDate> localDates) {
        return localDates.stream().filter(Objects::nonNull).max(Comparator.comparing(LocalDate::toEpochDay)).orElse(null);
    }

    public static String toString(LocalDate localDate, String patten) {
        if (localDate == null) return null;
        return localDate.format(DateTimeFormatter.ofPattern(patten));
    }

    public static String toShortString(LocalDate localDate) {
        if (localDate == null) return null;
        return localDate.format(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
    }

    public static String toChineseString(LocalDate localDate) {
        if (localDate == null) return null;
        return localDate.format(DateTimeFormatter.ofPattern("uuuu年 MM月 dd日"));
    }

    public static LocalDate getMonthFirstDay(LocalDate localDate) {
        if (localDate == null) return null;
        return LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
    }

    public static LocalDate getMonthFirstDay(LocalDate localDate, long monthToAdd) {
        if (localDate == null) return null;
        localDate = localDate.plusMonths(monthToAdd);
        return LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
    }

    public static LocalDate getCurrentFirstDayOfMonth() {
        return getFirstDayOfMonth(LocalDate.now());
    }

    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        if (date == null) return null;
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getCurrentFirstDayOfQuarter() {
        return getFirstDayOfQuarter(LocalDate.now());
    }

    public static LocalDate getFirstDayOfQuarter(LocalDate date) {
        if (date == null) return null;
        return date.with(date.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getCurrentFirstDayOfHalfYear() {
        return getFirstDayOfHalfYear(LocalDate.now());
    }

    public static LocalDate getFirstDayOfHalfYear(LocalDate date) {
        if (date == null) return null;
        return LocalDate.of(date.getYear(), (date.getMonthValue() > 6 ? 7 : 1), 1);
    }

    public static LocalDate getCurrentFirstDayOfYear() {
        return getFirstDayOfYear(LocalDate.now());
    }

    public static LocalDate getFirstDayOfYear(LocalDate date) {
        if (date == null) return null;
        return LocalDate.of(date.getYear(), 1, 1);
    }
}
