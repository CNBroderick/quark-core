package org.bklab.quark.util.time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocalDateTimeRange {
    private final String shortPattern = "uuuu-MM-dd HH:mm:ss";
    private final String dotPattern = "uuuu.MM.dd HH:mm:ss";
    private final String minimalPattern = "uuuuMMddHHmmss";

    private final LocalDateTime min;
    private final LocalDateTime max;

    public LocalDateTimeRange(LocalDateTime time1, LocalDateTime time2) {
        Objects.requireNonNull(time1, "LocalDateTimeRange min is null");
        Objects.requireNonNull(time2, "LocalDateTimeRange max is null");

        this.min = time1.isBefore(time2) ? time1 : time2;
        this.max = time2.isBefore(time1) ? time1 : time2;
    }

    public List<LocalDateTime> getIncludes(Duration duration) {
        List<LocalDateTime> localDateTimes = new ArrayList<>();
        LocalDateTime current = min;
        while (!current.isAfter(max)) {
            localDateTimes.add(current);
            current = current.plus(duration);
        }
        return localDateTimes;
    }

    public LocalDateTimeRange getCoincide(LocalDateTime start, LocalDateTime end) {
        return getCoincide(new LocalDateTimeRange(start, end));
    }

    public LocalDateTimeRange getCoincide(LocalDateTimeRange range) {
        return min.isBefore(range.min) && range.min.isBefore(max) || min.isBefore(range.max) && range.max.isBefore(max)
               ? new LocalDateTimeRange(new LocalDateTimeRange(min, range.min).max, new LocalDateTimeRange(max, range.max).min) : null;
    }

    public LocalDateTime getMin() {
        return min;
    }

    public LocalDateTime getMax() {
        return max;
    }
}
