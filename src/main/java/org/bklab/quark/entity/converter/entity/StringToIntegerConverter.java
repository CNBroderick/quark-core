package org.bklab.quark.entity.converter.entity;

import org.bklab.quark.util.text.StringExtractor;

public class StringToIntegerConverter implements StringToEntityConverter<Integer> {
    @Override
    public Integer apply(String s) {
        return StringExtractor.parseInt(s);
    }
}
