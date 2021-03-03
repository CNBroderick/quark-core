package org.bklab.quark.util.time;

import java.time.LocalTime;

public class LocalTimeRange {
        private LocalTime startTime;
        private LocalTime endTime;

        public LocalTimeRange(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public boolean isInRange(LocalTime localTime) {
            return startTime.isBefore(localTime) && endTime.isAfter(localTime);
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTimeRange setStartTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public LocalTimeRange setEndTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        @Override
        public String toString() {
            return String.format("%02d:%02d:%02d-%02d:%02d:%02d",
                    startTime.getHour(), startTime.getMinute(), startTime.getSecond(),
                    endTime.getHour(), endTime.getMinute(), endTime.getSecond());
        }
    }
