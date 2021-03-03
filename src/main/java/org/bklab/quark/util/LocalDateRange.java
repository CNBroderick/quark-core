/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-04-04 09:25:55
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.LocalDateRange
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;


@Deprecated
@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class LocalDateRange {

    private final String shortPattern = "uuuu-MM-dd";
    private final String dotPattern = "uuuu.MM.dd";
    private final String minimalPattern = "uuuuMMdd";
    private LocalDate start;
    private LocalDate end;
    private String description;
    private String formatPattern = "uuuu年 MM月 dd日";

    public LocalDateRange(LocalDate date1, LocalDate date2) {
        if (date1 == null) date1 = LocalDate.MIN;
        if (date2 == null) date2 = LocalDate.MAX;
        this.start = date1.isBefore(date2) ? date1 : date2;
        this.end = date1.isBefore(date2) ? date2 : date1;
    }

    public LocalDateRange(long start, long end) {
        this.start = start < end ? parse(start) : parse(end);
        this.end = start < end ? parse(end) : parse(start);
    }

    public LocalDateRange(LocalDate... localDates) {
        this(Arrays.asList(localDates));
    }

    public LocalDateRange(Collection<LocalDate> localDates) {
        if (localDates.size() < 1)
            throw new RuntimeException("最少传入一个 LocalDate");
        List<LocalDate> dates = localDates.stream().sorted(Comparator.comparing(LocalDate::toEpochDay)).collect(Collectors.toList());
        this.start = dates.get(0);
        this.end = dates.get(dates.size() - 1);
    }

    public static LocalDateRange create(LocalDate start, LocalDate end) {
        return new LocalDateRange(start, end);
    }

    public static LocalDateRange create(LocalDate... localDates) {
        return new LocalDateRange(localDates);
    }

    public static LocalDateRange create(Collection<LocalDate> localDates) {
        return new LocalDateRange(localDates);
    }

    public static LocalDateRange today() {
        return new LocalDateRange(LocalDate.now(), LocalDate.now());
    }

    @SafeVarargs
    public static LocalDateRange create(List<LocalDate>... localDates) {
        List<Collection<LocalDate>> list = Arrays.stream(localDates).filter(Objects::nonNull).collect(Collectors.toList());
        if (list.isEmpty()) throw new RuntimeException("最少传入一个非空Collection<LocalDate>");
        List<LocalDate> l = new ArrayList<>();
        list.forEach(l::addAll);
        return new LocalDateRange(l);
    }

    public static LocalDateRange create(long start, long end) {
        return new LocalDateRange(start, end);
    }

    public static LocalDateRange fromEpochDay(long start, long end) {
        return new LocalDateRange(LocalDate.ofEpochDay(start), LocalDate.ofEpochDay(end));
    }

    public static LocalDateRange merge(LocalDateRange... localDateRanges) {
        return merge(Arrays.asList(localDateRanges));
    }

    public static LocalDateRange merge(List<LocalDateRange> localDateRanges) {
        ArrayList<LocalDate> list = new ArrayList<>();
        list.addAll(localDateRanges.stream().filter(Objects::nonNull).map(LocalDateRange::getStart).collect(Collectors.toList()));
        list.addAll(localDateRanges.stream().filter(Objects::nonNull).map(LocalDateRange::getEnd).collect(Collectors.toList()));
        List<LocalDate> dates = list.stream().sorted(Comparator.comparing(LocalDate::toEpochDay)).collect(Collectors.toList());
        return LocalDateRange.create(dates.get(0), dates.get(dates.size() - 1));
    }

    private static boolean isWholeOneMonth(LocalDateRange range) {
        return range.getStart().getDayOfMonth() == 1 && isMonthLastDate(range.getEnd());
    }

    private static boolean isMonthLastDate(LocalDate date) {
        LocalDate lastDate = getMonthLastDate(date);
        return lastDate.getDayOfMonth() == date.getDayOfMonth();
    }

    public static LocalDate getMonthFirstDate(LocalDate dt) {
        return LocalDate.of(dt.getYear(), dt.getMonth(), 1);
    }

    public static LocalDate getMonthLastDate(LocalDate date) {
        int i = 0;
        while (true) {
            LocalDate nextDate = date.plusDays(i++);
            if (nextDate.getMonthValue() == date.getMonthValue()) continue;
            return nextDate.plusDays(-1);
        }
    }

    public boolean isInsideTheDateRange(LocalDate localDate) {
        return !localDate.isBefore(start) && !localDate.isAfter(end);
    }

    public LocalDate getMiddleLocalDateForRange() {
        return LocalDate.ofEpochDay(getDaysDifference() / 2 + start.toEpochDay());
    }

    private LocalDate parse(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public long includingDays() {
        return getDaysDifference() + 1;
    }

    public List<LocalDate> includingDate() {
        return LongStream.range(start.toEpochDay(), end.toEpochDay() + 1)
                .mapToObj(LocalDate::ofEpochDay).collect(Collectors.toList());
    }

    public int includingMonth() {
        return getMonthDifference() + 1;
    }

    public long getDaysDifference() {
        return end.toEpochDay() - start.toEpochDay();
    }

    public List<LocalDateRange> split(LocalDate localDate) {
        if (canSplit(localDate)) {
            return Arrays.asList(LocalDateRange.create(start, localDate),
                    LocalDateRange.create(localDate.plusDays(1), end));
        }
        throw new RuntimeException(String.format("日期超出范围或下一天为结束日期，" +
                "待拆分日期：%s，当前日期范围：%s。", toFormattedDate(localDate), toString()));
    }

    public boolean isToTheNextGrowthPoint(LocalDate startIncreaseDate) {
        if (start.isBefore(startIncreaseDate)) {
            return false;
        }

        LocalDate current = LocalDate.ofEpochDay(start.toEpochDay());
        while (!current.isAfter(end)) {
            if (startIncreaseDate.getMonthValue() == current.getMonthValue()
                    && startIncreaseDate.getDayOfMonth() == current.getDayOfMonth()) {
                return true;
            }
            current = current.plusDays(1);
        }

        return false;
    }

    public boolean canSplit(LocalDate localDate) {
        return isInsideTheDateRange(localDate) && localDate.plusDays(1).isBefore(end);
    }

    public long getStartEpochDay() {
        return start.toEpochDay();
    }

    public long getEndEpochDay() {
        return end.toEpochDay();
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDateRange setStart(LocalDate start) {
        this.start = start;
        return this;
    }

    public int getStartYear() {
        return start.getYear();
    }

    public int getEndYear() {
        return end.getYear();
    }

    public LocalDate getEnd() {
        return end;
    }

    public LocalDateRange setEnd(LocalDate end) {
        this.end = end;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateRange setDescription(String description) {
        this.description = description;
        return this;
    }

    public LocalDateRange setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
        return this;
    }

    private String toFormattedDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(formatPattern));
    }

    public String getFormattedStartDate() {
        return toFormattedDate(start);
    }

    public String getShortStartDate() {
        return toShortDate(start);
    }

    public String getDotStartDate() {
        return toDotDate(start);
    }

    public String getFormattedEndDate() {
        return toFormattedDate(end);
    }

    public String getShortEndDate() {
        return toShortDate(end);
    }

    public String getDotEndDate() {
        return toDotDate(end);
    }

    private String toShortDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(shortPattern));
    }

    private String toDotDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(dotPattern));
    }

    private String toMinimalDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(minimalPattern));
    }

    @Override
    public String toString() {
        return getFormattedStartDate() + " -- " + getFormattedEndDate();
    }

    public String toShortString() {
        return getShortStartDate() + "--" + getShortEndDate();
    }

    public String toDotString() {
        return getDotStartDate() + "-" + getDotEndDate();
    }

    public int getMonthDifference() {
        int i = 0;
        LocalDate current = LocalDate.of(start.getYear(), start.getMonthValue(), start.getDayOfMonth());
        while (end.isAfter(current)) {
            i++;
            current = current.plusMonths(1);
        }
        return i;
    }

    public float getMonthPreciseDifference() {
        float i = 0;
        LocalDate current = LocalDate.of(start.getYear(), start.getMonthValue(), start.getDayOfMonth());
        while (end.isAfter(current.plusMonths(1))) {
            i++;
            current = current.plusMonths(1);
        }
        i += 1.0 * (end.toEpochDay() - current.toEpochDay()) / end.lengthOfMonth();
        return i;
    }

    public int getYearsDifference() {
        return end.getYear() - start.getYear();
    }

    public float getYearsPreciseDifference() {
        LocalDate current = LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth());
        float y = 0f;
        while (!end.isBefore(current.plusYears(1))) {
            y += 1;
            current = current.plusYears(1);
        }
        y += 1.0 * (end.toEpochDay() - current.toEpochDay()) / end.lengthOfYear();
        return y;
    }

    public List<LocalDateRange> createNatureMonths() {
        List<LocalDateRange> ranges = new ArrayList<>();
        LocalDate current = getStart();
        while (!current.isAfter(getEnd())) {
            LocalDate monthEnd = getMonthLastDate(current);
            ranges.add(LocalDateRange.create(current, getEnd().isAfter(monthEnd) ? monthEnd : getEnd()));
            current = monthEnd.plusDays(1);
        }
        return ranges;
    }

    public LocalDate getMonthFirstDate(LocalDate date, int nMonth) {
        LocalDate dt = date.plusMonths(nMonth);
        return LocalDate.of(dt.getYear(), dt.getMonth(), 1);
    }

    public List<LocalDateRange> partitionNatural(int nStep) {
        GroupHelper helper = new GroupHelper(nStep);
        createNatureMonths().forEach(helper::addRange);
        return helper.createPartitions();
    }

    public List<LocalDateRange> partitionHalfAccordingNatural(int nStep) {
        if (getMonthDifference() == 1) return Collections.singletonList(this);
        if (start.getDayOfMonth() == 1) return partitionNatural(nStep);
        List<LocalDateRange> ranges = new ArrayList<>();

        LocalDate firstEnd = start.plusMonths(nStep).withDayOfMonth(1).minusDays(1);
        if (firstEnd.isAfter(end)) {
            ranges.add(LocalDateRange.create(start, end));
        } else {
            ranges.add(LocalDateRange.create(start, firstEnd));
            ranges.addAll(LocalDateRange.create(firstEnd.plusDays(1), end).partitionNatural(nStep));
        }

        return ranges;
    }

    public List<LocalDateRange> partitionNonNatural(int nStep) {
        List<LocalDateRange> ranges = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            LocalDate thisEnd = current.plusMonths(nStep).minusDays(1);
            ranges.add(LocalDateRange.create(current, end.isAfter(thisEnd) ? thisEnd : end));
            current = thisEnd.plusDays(1);
        }
        return ranges;
    }

    static class GroupHelper {
        int groupSize;
        int index = 0;
        List<LocalDateRange> ranges = new ArrayList<>();
        private LocalDateRange firstRange;
        private LocalDateRange lastRange;

        public GroupHelper(int groupSize) {
            this.groupSize = groupSize;
        }

        public void addRange(LocalDateRange range) {
            if (isWholeOneMonth(range)) {
                ranges.add(range);
            } else {
                if (ranges.isEmpty()) {
                    firstRange = range;
                } else {
                    lastRange = range;
                }
            }
        }

        public LocalDateRange getLast() {
            return lastRange;
        }

        public LocalDateRange getFirst() {
            return firstRange;
        }

        public List<LocalDateRange> nextGroup() {
            List<LocalDateRange> group = new ArrayList<>();
            for (int i = 0; i < this.groupSize; i++) {
                if (index < ranges.size())
                    group.add(this.ranges.get(index++));
            }
            return group;
        }

        public List<LocalDateRange> createPartitions() {
            List<LocalDateRange> finalRanges = new ArrayList<>();
            if (getFirst() != null) {
                finalRanges.add(getFirst());
            }
            while (true) {
                List<LocalDateRange> group = nextGroup();
                if (group == null || group.isEmpty()) break;
                finalRanges.add(merge(group.toArray(new LocalDateRange[0])));
            }
            if (getLast() != null) {
                finalRanges.add(getLast());
            }
            return finalRanges;
        }
    }
}
