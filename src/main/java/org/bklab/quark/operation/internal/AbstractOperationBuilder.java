package org.bklab.quark.operation.internal;

import dataq.core.operation.AbstractOperation;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class AbstractOperationBuilder<E extends AbstractOperationBuilder<E>> implements IAbstractOperationBuilder<E> {

    private final Map<String, Object> parameterMap = new LinkedHashMap<>();
    private final List<BiConsumer<AbstractOperation, Exception>> exceptionConsumers = new ArrayList<>();

    public AbstractOperationBuilder() {
        beforeInitSafely();
        exceptionConsumers.add((operation, exception) ->
                logger().error(String.format("执行[%s]时错误，错误内容：\n%s参数列表：\n%s错误信息：\n",
                        operation.getContext().getOperationName(),
                        Optional.ofNullable(exception.getLocalizedMessage()).orElse(exception.getMessage()),
                        getParameterMapPrettyJson(operation.getContext())
                ), exception)
        );
    }

    @Override
    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    @Override
    public List<BiConsumer<AbstractOperation, Exception>> getExceptionConsumers() {
        return exceptionConsumers;
    }
}
