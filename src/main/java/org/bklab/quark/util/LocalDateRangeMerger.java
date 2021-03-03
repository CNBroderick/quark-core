/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-04-03 13:20:09
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.LocalDateRangeMerger
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@Deprecated
public class LocalDateRangeMerger {

    public static List<LocalDateRange> forHead(List<LocalDateRange> localDateRanges, int combinedLength) {
        if (combinedLength >= localDateRanges.size()) {
            return new ArrayList<>(Collections.singletonList(LocalDateRange.merge(localDateRanges)));
        } else {
            List<LocalDateRange> ranges = new ArrayList<>();
            ranges.add(LocalDateRange.merge(localDateRanges.subList(0, combinedLength + 1)));
            ranges.addAll(localDateRanges.subList(combinedLength + 1, localDateRanges.size()));
            return ranges;
        }
    }

    public static List<LocalDateRange> forTail(List<LocalDateRange> localDateRanges, int combinedLength) {
        if (combinedLength >= localDateRanges.size()) {
            return new ArrayList<>(Collections.singletonList(LocalDateRange.merge(localDateRanges)));
        } else {
            List<LocalDateRange> ranges = new ArrayList<>(
                    localDateRanges.subList(0, localDateRanges.size() - combinedLength - 1));
            ranges.add(LocalDateRange.merge(localDateRanges.subList(
                    localDateRanges.size() - 1 - combinedLength, localDateRanges.size())));
            return ranges;
        }
    }

    public static List<LocalDateRange> forAll(
            List<LocalDateRange> localDateRanges, int combinedHeadLength, int combinedTailLength) {
        localDateRanges = forHead(localDateRanges, combinedHeadLength);
        return forTail(localDateRanges, combinedTailLength);
    }

}
