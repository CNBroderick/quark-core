package org.bklab.quark.entity.converter.entity;

import org.bklab.quark.util.text.StringExtractor;

public class StringToDoubleConverter implements StringToEntityConverter<Double> {
    @Override
    public Double apply(String s) {
        return StringExtractor.parseDouble(s);
    }
}
