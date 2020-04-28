package org.bklab.quark.entity.converter;

import org.bklab.quark.entity.StringToEntityConverter;
import org.bklab.quark.util.StringExtractor;

public class StringToLongConverter implements StringToEntityConverter<Long> {
    @Override
    public Long apply(String s) {
        return StringExtractor.parseLong(s);
    }
}
