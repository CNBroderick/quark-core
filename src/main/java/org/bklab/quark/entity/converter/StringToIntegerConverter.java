package org.bklab.quark.entity.converter;

import org.bklab.quark.entity.StringToEntityConverter;
import org.bklab.quark.util.StringExtractor;

public class StringToIntegerConverter implements StringToEntityConverter<Integer> {
    @Override
    public Integer apply(String s) {
        return StringExtractor.parseInt(s);
    }
}
