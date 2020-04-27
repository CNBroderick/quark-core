/*
 * Class: org.bklab.util.search.common.NumberRangeSearcher
 * Modify date: 2020/3/20 上午10:54
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.search.common;

import org.bklab.util.StringExtractor;

public class NumberRangeSearcher {
    public boolean match(double number, String keyword) {
        if (keyword.contains(",") || keyword.contains("，")) {
            return isInRange(keyword.replaceAll("，", ","), number);
        }
        if (keyword.contains(">")) {
            return isBeyond(keyword, number);
        }
        if (keyword.contains("<")) {
            return isLess(keyword, number);
        }
        return false;
    }

    private boolean isInRange(String keyword, double area) {
        String[] s = keyword.split(",");
        if (s.length > 1) {
            double a = StringExtractor.parseDouble(s[0]);
            double b = StringExtractor.parseDouble(s[1]);
            return area >= Math.min(a, b) && area <= Math.max(a, b);
        } else if (s.length == 1) {
            if (keyword.startsWith(",")) {
                return area <= StringExtractor.parseDouble(s[0]);
            }
            if (keyword.endsWith(",")) {
                return StringExtractor.parseDouble(s[0]) <= area;
            }
        }
        return false;
    }

    private boolean isBeyond(String keyword, double area) {
        return StringExtractor.parseDouble(keyword) <= area;
    }

    private boolean isLess(String keyword, double area) {
        return StringExtractor.parseDouble(keyword) >= area;
    }
}
