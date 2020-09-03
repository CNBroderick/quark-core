package org.bklab.quark.entity.converter.entity;

import org.bklab.quark.util.text.StringExtractor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class StringToLocalDateConverter implements StringToEntityConverter<LocalDate> {

    /**
     * @param date support
     *             <ul>
     *              <li>EpochDay</li>
     *              <li>yyyy-MM-dd</li>
     *              <li>yyyy-M-dd</li>
     *              <li>yyyy-MM-d</li>
     *              <li>yyyy-M-dd</li>
     *             </ul>
     * @return formatted LocalDate
     */
    @Override
    public LocalDate apply(String date) {
        try {
            if (Pattern.matches("\\d+", date)) {
                return LocalDate.ofEpochDay(StringExtractor.parseLong(date));
            }

            date = date.replaceAll("^(\\d{4})(.)(\\d)(\\2)(\\d{1,2})$", "$10$3$5").replaceAll("^(\\d{4})(.)(\\d{2})(\\2)(\\d{1,2})$", "$1$3$5").replaceAll("^(\\d{6})(\\d{1})$", "$10$2");
            return LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);//yyyyMMdd
        } catch (Exception e) {
            throw new UnsupportedOperationException("不支持的格式：" + date + ", 请将年月日用减号分割。例如：2020-2-2");
        }
    }
}
