/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-03-23 16:58:30
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.service.Service
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.service;

import dataq.core.operation.AbstractOperation;
import dataq.core.operation.OperationResult;
import org.bklab.quark.element.HasExceptionConsumers;
import org.bklab.quark.element.HasSaveListeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
/*
 * if implements, insert flow code to implement class:
 *     private final List<Consumer<Exception>> exceptionConsumers = new ArrayList<>();
 *     private final List<Consumer<OperationResult>> saveListeners = new ArrayList<>();
 */
public interface Service<T extends Service<T>> extends HasSaveListeners<OperationResult, T>, HasExceptionConsumers<T> {


    String getCurrentUserId();

    default T addExceptionConsumer(Consumer<Exception> exceptionConsumer) {
        if (exceptionConsumer != null) getExceptionConsumers().add(exceptionConsumer);
        return (T) this;
    }

    default T addSuccessConsumer(Consumer<OperationResult> successConsumer) {
        if (successConsumer != null) getSaveListeners().add(successConsumer);
        return (T) this;
    }

    default void callSaveListeners(OperationResult operationResult) {
        getSaveListeners().forEach(x -> x.accept(operationResult));
    }

    default void callExceptionConsumers(Exception exception) {
        getExceptionConsumers().forEach(x -> x.accept(exception));
    }

    default boolean executeOperation(HasAbstractOperation operation, Map<String, ?> params) {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        insertParams(operation, params).execute().ifSuccess(operationResult -> {
            isSuccess.set(true);
            callSaveListeners(operationResult);
        }).ifException(this::callExceptionConsumers);
        return isSuccess.get();
    }

    default <E> E queryList(HasAbstractOperation operation) {
        return queryList(operation, new AtomicReference<>(), new HashMap<>());
    }

    default <E> E queryList(HasAbstractOperation operation, AtomicReference<E> atomicReference, Map<String, ?> params) {
        insertParams(operation, params).execute().ifOK(atomicReference::set).ifException(this::callExceptionConsumers);
        return atomicReference.get();
    }

    default <E> E queryList(HasAbstractOperation operation, Map<String, ?> params) {
        AtomicReference<E> atomicReference = new AtomicReference<>();
        insertParams(operation, params).execute().ifOK(atomicReference::set).ifException(this::callExceptionConsumers);
        return atomicReference.get();
    }

    default <E> E queryEntity(HasAbstractOperation operation, Map<String, ?> params) {
        AtomicReference<List<E>> reference = new AtomicReference<>();
        insertParams(operation, params).execute().ifOK(reference::set).ifException(this::callExceptionConsumers);
        return Optional.of(reference.get()).flatMap(a -> a.stream().findFirst()).orElse(null);
    }

    default <E> E queryObject(HasAbstractOperation operation) {
        AtomicReference<E> reference = new AtomicReference<>();
        insertParams(operation, new HashMap<>()).execute().ifOK(reference::set).ifException(this::callExceptionConsumers);
        return reference.get();
    }

    default <E> E queryObject(HasAbstractOperation operation, E defaultValue) {
        AtomicReference<E> reference = new AtomicReference<>(defaultValue);
        insertParams(operation, new HashMap<>()).execute().ifOK(reference::set).ifException(this::callExceptionConsumers);
        return reference.get();
    }

    default <E> E queryObject(HasAbstractOperation operation, Map<String, Object> params) {
        AtomicReference<E> reference = new AtomicReference<>();
        insertParams(operation, params).execute().ifOK(reference::set).ifException(this::callExceptionConsumers);
        return reference.get();
    }

    default <E> E queryObject(HasAbstractOperation operation, E defaultValue, Map<String, ?> params) {
        AtomicReference<E> reference = new AtomicReference<>(defaultValue);
        insertParams(operation, params).execute().ifOK(reference::set).ifException(this::callExceptionConsumers);
        return reference.get();
    }

    default <E> E queryObject(HasAbstractOperation operation, Supplier<Map<String, Object>> params) {
        return queryObject(operation, params.get());
    }

    default <E> E queryObject(HasAbstractOperation operation, E defaultValue, Supplier<Map<String, ?>> params) {
        return queryObject(operation, defaultValue, params.get());
    }

    default AbstractOperation insertParams(HasAbstractOperation hasAbstractOperation, Map<String, ?> params) {
        AbstractOperation operation = hasAbstractOperation.createAbstractOperation();
        operation.setParam("opr", getCurrentUserId());
        if (params != null) params.forEach(operation::setParam);
        return operation;
    }
}
