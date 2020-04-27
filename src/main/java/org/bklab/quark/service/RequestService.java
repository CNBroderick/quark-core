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

import dataq.core.operation.OperationResult;
import org.bklab.quark.common.request.IRequestFactory;
import org.bklab.quark.common.request.Request;
import org.bklab.quark.common.request.Response;
import org.bklab.quark.element.HasExceptionConsumers;
import org.bklab.quark.element.HasSaveListeners;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
/*
 * if implements, insert flow code to implement class:
 *     private final List<Consumer<Exception>> exceptionConsumers = new ArrayList<>();
 *     private final List<Consumer<OperationResult>> saveListeners = new ArrayList<>();
 */
public interface RequestService<T extends RequestService<T>> extends HasSaveListeners<OperationResult, T>, HasExceptionConsumers<T> {


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

    default boolean executeOperation(IRequestFactory operation, Map<String, ?> params) {
        Request request = operation.createRequest();
        request.param("opr", getCurrentUserId()).param(params);
        Response response = checkException(request.execute());
        if (response.isException()) {
            callExceptionConsumers(response.getException());
            return false;
        }
        return true;
    }

    default <E> E queryList(IRequestFactory operation) {
        return queryList(operation, new HashMap<>());
    }

    default <E> E queryList(IRequestFactory operation, Map<String, ?> params) {
        return Optional.ofNullable((E) execute(operation, params)).orElse((E) new ArrayList<>());
    }

    default <E> E queryEntity(IRequestFactory operation, Map<String, ?> params) {
        List<E> list = execute(operation, params);
        return Optional.of(list).flatMap(a -> a.stream().findFirst()).orElse(null);
    }

    default <E> E queryObject(IRequestFactory operation) {
        return queryObject(operation, new HashMap<>());
    }

    default <E> E queryObject(IRequestFactory operation, E defaultValue) {
        return queryObject(operation, defaultValue, new HashMap<>());
    }

    default <E> E queryObject(IRequestFactory operation, Map<String, Object> params) {
        return execute(operation, params);
    }

    default <E> E queryObject(IRequestFactory operation, E defaultValue, Map<String, ?> params) {
        return Optional.ofNullable((E) execute(operation, params)).orElse(defaultValue);
    }

    default <E> E queryObject(IRequestFactory operation, Supplier<Map<String, Object>> params) {
        return queryObject(operation, params.get());
    }

    default <E> E queryObject(IRequestFactory operation, E defaultValue, Supplier<Map<String, ?>> params) {
        return queryObject(operation, defaultValue, params.get());
    }

    default <E> E execute(IRequestFactory buildingOperationEnum, Map<String, ?> params) {
        Response response = buildingOperationEnum.createRequest()
                .param("opr", getCurrentUserId()).param(params).execute();

        if (response.isException()) {
            callExceptionConsumers(response.getException());
        } else if (response.isNotFound()) {
            callExceptionConsumers(new NoSuchElementException("not found."));
        }

        return response.asObject();
    }

    default Response checkException(Response response) {
        if (response.isException()) {
            callExceptionConsumers(response.getException());
        } else if (response.isNotFound()) {
            callExceptionConsumers(new NoSuchElementException("not found."));
        }
        return response;
    }
}
