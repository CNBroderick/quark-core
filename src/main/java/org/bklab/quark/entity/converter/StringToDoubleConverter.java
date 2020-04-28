package org.bklab.quark.entity.converter;

import org.bklab.quark.entity.StringToEntityConverter;
import org.bklab.quark.util.StringExtractor;

public class StringToDoubleConverter implements StringToEntityConverter<Double> {
    @Override
    public Double apply(String s) {
        return StringExtractor.parseDouble(s);
    }
}
