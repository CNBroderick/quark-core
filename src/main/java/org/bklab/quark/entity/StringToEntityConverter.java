package org.bklab.quark.entity;

import java.util.function.Function;

public interface StringToEntityConverter<T> extends Function<String, T> {
    @Override
    T apply(String string);
}
