/*
 * Class: org.bklab.util.UrlEncoder
 * Modify date: 2020/3/20 上午10:25
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Deprecated
public class UrlEncoder {

    public static String encode(String text) {

        try {
            return URLEncoder.encode(text, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }
    }
}
