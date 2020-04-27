/*
 * Class: org.bklab.util.search.common.SplitKeywordMatcher
 * Modify date: 2020/3/20 上午10:25
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.search.common;

import java.util.Locale;
import java.util.function.BiPredicate;

public class SplitKeywordMatcher implements BiPredicate<String, String> {
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param source source word as the first input argument
     * @param key    keyword as the second input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(String source, String key) {
        if (key.isBlank()) return true;
        char[] k = key.toLowerCase(Locale.CHINA).toCharArray();
        int i = 0;
        int l = key.length();
        for (char s : source.toLowerCase(Locale.CHINA).toCharArray()) {
            while (i < k.length && k[i] == ' ') i++;
            if (i >= l) break;
            if (i < k.length && k[i] == s) {
                i++;
            }
        }
        return i == k.length;
    }
}
