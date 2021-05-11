package org.bklab.quark.operation.core;

import dataq.core.operation.OperationContext;

import java.util.Map;

@SuppressWarnings("unchecked")
public interface HasOperationContext<E extends HasOperationContext<E>> {

    default E addParameter(Map<String, Object> map) {
        map.forEach(this::addParameter);
        return thisObject();
    }

    default E addParameter(String name, Object value) {
        getContext().setParam(name, value);
        return thisObject();
    }

    private E thisObject() {
        return (E) this;
    }

    OperationContext getContext();
}
