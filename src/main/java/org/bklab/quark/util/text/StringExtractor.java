/*
 * Class: org.bklab.util.StringExtractor
 * Modify date: 2020/3/20 上午10:25
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
public class StringExtractor {
    public static float parseFloat(String s) {
        String f = getRealNumber(s);
        return f.length() == 0 ? 0 : Float.parseFloat(f);
    }

    public static double parseDouble(String s) {
        String d = getRealNumber(s);
        return d.length() == 0 ? 0 : Double.parseDouble(d);
    }

    public static double parseDouble(String s, int scale) {
        String d = getRealNumber(s);
        return d.length() == 0 ? 0 : new BigDecimal(d).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static String getRealNumber(String s) {
        if (s == null) return "";
        s = s.chars().mapToObj(c -> (char) c).filter(c -> c == 0x2e || c == 0x2D || c > 0x2F && c < 0x3A)
                .map(String::valueOf).collect(Collectors.joining());
        s = s.startsWith("-") ? "-" + s.replaceAll("-", "") : s.replaceAll("-", "");
        int index = s.indexOf(0x2e) + 1;
        int length = s.length();
        if (length == 1) {
            return s.equals(".") || s.equals("-") ? "0" : s;
        }
        if (length > index) {
            return s.substring(0, index) + s.substring(index).replaceAll("\\.", "");
        }
        return s;
    }


    public static int parseInt(String s) {
        if (s == null) return 0;
        s = getInteger(s);
        if (s.length() > 0) {
            return Integer.parseInt(s);
        }
        return 0;
    }

    public static String getInteger(String s) {
        if (s == null) return "";
        s = s.chars().mapToObj(c -> (char) c).filter(c -> c > 0x2F && c < 0x3A || c == 0x2D)
                .map(String::valueOf).collect(Collectors.joining());
        if (s.length() == 0) return "";
        return s.startsWith("-") ? "-" + s.replaceAll("-", "") : s.replaceAll("-", "");
    }

    public static String getIntegerWithSuffix(String s, String suffix) {
        s = getInteger(s);
        return s.length() > 0 ? s + suffix : "";
    }

    public static String getIntegerWithPrefix(String s, String prefix) {
        s = getInteger(s);
        return s.length() > 0 ? prefix + s : "";
    }

    public static String getInteger(String prefix, String s, String suffix) {
        s = getInteger(s);
        return s.length() > 0 ? prefix + s + suffix : "";
    }

    public static long parseLong(String s) {
        if (s == null) return 0;
        return Long.parseLong(s.chars().mapToObj(c -> (char) c).filter(c -> c > 0x2F && c < 0x3A)
                .map(String::valueOf).collect(Collectors.joining()));
    }
}
