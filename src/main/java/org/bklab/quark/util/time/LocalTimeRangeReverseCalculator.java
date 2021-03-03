package org.bklab.quark.util.time;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalTimeRangeReverseCalculator {

    private final List<LocalTimeRange> sources = new ArrayList<>();

    public static void main(String[] args) {
        LocalTimeRangeReverseCalculator calculator = new LocalTimeRangeReverseCalculator();
        calculator.add(LocalTime.of(12, 0), LocalTime.of(20, 0));
        System.out.println(calculator.mergeDuplicates());
        System.out.println(calculator.reverse());
        System.out.println(calculator.reverse(LocalTime.of(9,0), LocalTime.of(18,0)));
    }

    public LocalTimeRangeReverseCalculator() {
    }

    public LocalTimeRangeReverseCalculator(List<LocalTimeRange> localTimeRanges) {
        this.sources.addAll(localTimeRanges);
    }

    public LocalTimeRangeReverseCalculator add(List<LocalTimeRange> localTimeRanges) {
        this.sources.addAll(localTimeRanges);
        return this;
    }

    public LocalTimeRangeReverseCalculator add(LocalTimeRange... localTimeRanges) {
        Collections.addAll(this.sources, localTimeRanges);
        return this;
    }

    public LocalTimeRangeReverseCalculator add(LocalTime start, LocalTime end) {
        this.sources.add(new LocalTimeRange(start, end));
        return this;
    }

    private List<LocalTimeRange> mergeDuplicates() {
        if (sources.size() < 2) return sources;
        LocalTimeRange[] ranges = sources.stream()
                .sorted(Comparator.comparingInt(a -> a.getStartTime().toSecondOfDay()))
                .collect(Collectors.toList()).toArray(new LocalTimeRange[]{});
        List<LocalTimeRange> merged = new ArrayList<>();
        merged.add(ranges[0]);
        for (int i = 1; i < ranges.length; i++) {
            LocalTimeRange last = merged.get(merged.size() - 1);
            if (last.getEndTime().isBefore(ranges[i].getStartTime())) {
                merged.add(ranges[i]);
            } else {
                merged.remove(last);
                merged.add(new LocalTimeRange(
                        min(last.getStartTime(), last.getEndTime(), ranges[i].getStartTime(), ranges[i].getEndTime()),
                        max(last.getStartTime(), last.getEndTime(), ranges[i].getStartTime(), ranges[i].getEndTime())
                ));
            }
        }
        return merged;
    }

    private LocalTime min(LocalTime... localTimes) {
        return Stream.of(localTimes).min(Comparator.comparingInt(LocalTime::toSecondOfDay)).orElse(null);
    }

    private LocalTime max(LocalTime... localTimes) {
        return Stream.of(localTimes).max(Comparator.comparingInt(LocalTime::toSecondOfDay)).orElse(null);
    }


    public List<LocalTimeRange> reverse() {
        return reverse(LocalTime.MIN,  LocalTime.MAX);
    }

    public List<LocalTimeRange> reverse(LocalTime startPosition, LocalTime endPosition) {
        List<LocalTimeRange> ranges = mergeDuplicates();
        List<LocalTimeRange> reverses = new ArrayList<>();

        LocalTime current = startPosition;
        for (LocalTimeRange range : ranges) {
            if (range.getStartTime().isAfter(current)) {
                reverses.add(new LocalTimeRange(current, range.getStartTime().minusSeconds(1)));
                current = range.getEndTime().plusSeconds(1);
            }
        }

        if (current.isBefore(endPosition)) reverses.add(new LocalTimeRange(current, LocalTime.MAX));

        return reverses;
    }

}
