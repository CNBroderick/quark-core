/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-04-17 18:21:21
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.EnumFactory
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EnumFactory<T extends Enum<T>> implements Supplier<T[]> {

    private final T[] instances;
    private final Class<T> classReference;

    public EnumFactory(Class<T> classReference) throws Exception {
        Objects.requireNonNull(classReference, "类不能为null");
        this.classReference = classReference;
        this.instances = invokeInstance();
    }

    public EnumFactory(String referencePath) throws Exception {
        Objects.requireNonNull(referencePath, "类路径不能为null");
        this.classReference = (Class<T>) Class.forName(referencePath);
        this.instances = invokeInstance();
    }

    private T[] invokeInstance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (!classReference.isEnum()) throw new IllegalArgumentException("类[" + classReference.getName() + "]非Enum");
        return (T[]) classReference.getMethod("values").invoke(null);

    }

    public Optional<T> getInstance(String name) {
        for (T instance : instances) {
            if (instance.name().equals(name)) {
                return Optional.of(instance);
            }
        }
        return Optional.empty();
    }

    public Optional<T> getInstance(String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalAccessException {
        Field field = classReference.getDeclaredField(fieldName);
        for (T instance : instances) {
            field.setAccessible(true);
            if (field.get(instance).equals(fieldValue)) {
                return Optional.ofNullable(instance);
            }
        }
        return Optional.empty();
    }

    public Optional<T> getInstance(int fieldIndex, Object fieldValue) throws IllegalAccessException {
        Field field = classReference.getDeclaredFields()[fieldIndex];
        for (T instance : instances) {
            field.setAccessible(true);
            if (field.get(instance).equals(fieldValue)) {
                return Optional.ofNullable(instance);
            }
        }
        return Optional.empty();
    }

    private Optional<T> getInstance(Field field, Predicate<T> isThisInstancePredicate) {
        for (T instance : instances) {
            field.setAccessible(true);
            if (isThisInstancePredicate.test(instance)) {
                return Optional.ofNullable(instance);
            }
        }
        return Optional.empty();
    }

    public <E> Optional<Object> getInstance(String fieldName, Predicate<E> isThisInstancePredicate) throws NoSuchFieldException, IllegalAccessException {
        Field field = classReference.getDeclaredField(fieldName);
        for (T instance : instances) {
            field.setAccessible(true);
            if (isThisInstancePredicate.test((E) field.get(instance))) {
                return Optional.ofNullable(instance);
            }
        }
        return Optional.empty();
    }

    public Optional<T> getInstance(Predicate<T> isThisInstancePredicate) {
        for (T instance : instances) {
            if (isThisInstancePredicate.test(instance)) {
                return Optional.ofNullable(instance);
            }
        }
        return Optional.empty();
    }

    public T[] getInstances() {
        return instances;
    }

    @Override
    public T[] get() {
        return instances;
    }
}
