package org.bklab.quark.entity.converter.entity;

import org.bklab.quark.util.text.StringExtractor;

public class StringToLongConverter implements StringToEntityConverter<Long> {
    @Override
    public Long apply(String s) {
        return StringExtractor.parseLong(s);
    }
}
