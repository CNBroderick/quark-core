package org.bklab.quark.entity.converter.entity;

import org.bklab.quark.util.text.StringExtractor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringToLocalTimeConverter implements StringToEntityConverter<LocalTime> {

    /**
     * @param date support
     *             <ul>
     *              <li>EpochSecond</li>
     *              <li>HH:mm</li>
     *              <li>HH:mm:ss</li>
     *              <li>HH:mm:ss:S</li>
     *              <li>HH:mm:ss:SS</li>
     *              <li>HH:mm:ss:SSS</li>
     *             </ul>
     * @return formatted LocalDateTime
     */
    @Override
    public LocalTime apply(String date) {
        try {
            if (Pattern.matches("\\d+", date)) {
                return LocalTime.ofSecondOfDay(StringExtractor.parseLong(date));
            }
            int i = 0;
            for (char c : date.toCharArray()) {
                if (c == ':') i++;
            }
            switch (i) {
                case 0:
                    return LocalTime.parse(date, DateTimeFormatter.ofPattern("HH"));
                case 1:
                    return LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm"));
                case 3:
                    return LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm:ss:" + (IntStream.range(date.lastIndexOf(':'), date.length() - 1).mapToObj(b -> "S").collect(Collectors.joining()))));
                default:
                    return LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm:ss"));

            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("不支持的格式：" + date + ", 请使用如下格式：HH:mm HH:mm:ss HH:mm:ss:S HH:mm:ss:SS HH:mm:ss:SSS");
        }
    }
}
