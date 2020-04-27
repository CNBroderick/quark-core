package org.bklab.quark.common.request;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Request {
    private final Map<String, Object> parameterMap = new LinkedHashMap<>(4);
    private final List<Consumer<?>> successConsumers = new ArrayList<>();
    private String operationName;
    private Class<?> creator;
    private IRequestExecutor executor;
    private Consumer<Exception> exceptionConsumer = System.err::print;
    private Consumer<Response> notfoundConsumer = System.err::print;

    public Class<?> getCreator() {
        return creator;
    }

    public Request setCreator(Class<?> creator) {
        this.creator = creator;
        return this;
    }

    public IRequestExecutor getExecutor() {
        return executor;
    }

	public Request setExecutor(IRequestExecutor executor) {
		this.executor = executor;
		return this;
	}

	public Response execute() {
        IRequestExecutor executor = getExecutor();
        if (executor == null) return Response.fromException("未定义执行器[" + IRequestExecutor.class.getName() + "]");

        Response response = executor.execute(this);
        if (response.isSuccess()) {
            if (response.isNotFound())
                notfoundConsumer.accept(response);
            else
                successConsumers.forEach(consumer -> consumer.accept(response.asObject()));
        } else
            exceptionConsumer.accept(response.getException());

        return response;
    }

    public String getOperationName() {
        return operationName;
    }

    public Request setOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public Request param(Map<String, ?> param) {
        if (param != null) parameterMap.putAll(param);
        return this;
    }

    public Request param(String parameterName, Object parameterValue) {
        parameterMap.put(parameterName, parameterValue);
        return this;
    }

    public Request setParam(Map<String, ?> param) {
        if (param != null) parameterMap.putAll(param);
        return this;
    }

    public Request setParam(String parameterName, Object parameterValue) {
        parameterMap.put(parameterName, parameterValue);
        return this;
    }

    public Map<String, Object> getParams() {
        return parameterMap;
    }

    public <T> Request addSuccessConsumer(Consumer<T> successConsumer) {
        successConsumers.add(successConsumer);
        return this;
    }

    public Request setNotFoundConsumer(Consumer<Response> notFoundConsumer) {
        this.notfoundConsumer = notFoundConsumer;
        return this;
    }

    public Request setExceptionConsumer(Consumer<Exception> exceptionConsumer) {
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    public <E> E getParam(String parameterName) {
        //noinspection unchecked
        return (E) parameterMap.get(parameterName);
    }

    public String getString(String parameterName) {
        return (String) parameterMap.get(parameterName);
    }

    @Override
    public String toString() {
        return parameterMap.keySet().stream()
                .map(k -> k + "=" + parameterMap.get(k))
                .collect(Collectors.joining(" ", "request parameters[", "]"));
    }
}
