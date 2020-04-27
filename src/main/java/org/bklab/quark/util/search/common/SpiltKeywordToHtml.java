/*
 * Class: org.bklab.util.search.common.SpiltKeywordToHtml
 * Modify date: 2020/3/20 上午10:25
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.search.common;

import java.util.Locale;
import java.util.function.BiFunction;

public class SpiltKeywordToHtml implements BiFunction<String, String, String> {

    private static final String head = "<span style='color:red;'>";
    private static final String tail = "</span>";

    /**
     * Applies this function to the given arguments.
     *
     * @param source source word as the first function argument
     * @param key    key word as the second function argument
     * @return the function result is a html by red key
     */
    @Override
    public String apply(String source, String key) {
        StringBuilder html = new StringBuilder("<span>");
        char[] k = key.toLowerCase(Locale.CHINA).toCharArray();
        int i = 0;
        for (char s : source.toLowerCase(Locale.CHINA).toCharArray()) {
            while (i < k.length && k[i] == ' ') i++;
            if (i < k.length && k[i] == s) {
                html.append(head).append(s).append(tail);
                i++;
            } else {
                html.append(s);
            }
        }
        return html.append("</span>").toString().replaceAll("</span><span style='color:red;'>", "");
    }
}
