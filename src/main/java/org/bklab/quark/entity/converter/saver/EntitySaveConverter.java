package org.bklab.quark.entity.converter.saver;

import java.util.function.Function;

public interface EntitySaveConverter<T> extends Function<T, Object> {
    @Override
    Object apply(T entity);
}
