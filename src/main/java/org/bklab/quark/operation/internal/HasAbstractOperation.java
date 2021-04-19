package org.bklab.quark.operation.internal;

import dataq.core.operation.AbstractOperation;

import java.util.Map;

public interface HasAbstractOperation {

    AbstractOperation createAbstractOperation();

    String getOperationName();

    default AbstractOperation createAbstractOperation(Map<String, Object> parameterMap) {
        AbstractOperation abstractOperation = createAbstractOperation();
        parameterMap.forEach(abstractOperation::setParam);
        return abstractOperation;
    }

}
