package org.bklab.quark.entity.converter;

import org.bklab.quark.entity.StringToEntityConverter;
import org.bklab.quark.util.StringExtractor;

public class StringToFloatConverter implements StringToEntityConverter<Float> {
    @Override
    public Float apply(String s) {
        return StringExtractor.parseFloat(s);
    }
}
