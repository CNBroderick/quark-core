package org.bklab.quark.entity.converter.entity;

import org.bklab.quark.util.text.StringExtractor;

public class StringToFloatConverter implements StringToEntityConverter<Float> {
    @Override
    public Float apply(String s) {
        return StringExtractor.parseFloat(s);
    }
}
