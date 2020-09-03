package org.bklab.quark.entity.converter.entity;

import org.bklab.quark.util.text.StringExtractor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class StringToLocalDateTimeConverter implements StringToEntityConverter<LocalDateTime> {

    /**
     * @param date support
     *             <ul>
     *              <li>EpochSecond</li>
     *              <li>yyyy-MM-dd HH:mm:ss</li>
     *             </ul>
     * @return formatted LocalDateTime
     */
    @Override
    public LocalDateTime apply(String date) {
        try {
            if (Pattern.matches("\\d+", date)) {
                return LocalDateTime.ofEpochSecond(StringExtractor.parseLong(date), 0, ZoneOffset.ofHours(8));
            }
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new UnsupportedOperationException("不支持的格式：" + date + ", 请使用如下格式：yyyy-MM-dd HH:mm:ss");
        }
    }
}
