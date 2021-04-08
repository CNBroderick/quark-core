package org.bklab.quark.common.dataq.core.operation.error;

import dataq.core.operation.AbstractOperation;
import dataq.core.operation.OperationResult;
import org.slf4j.LoggerFactory;

public class DataQExceptionOperation extends AbstractOperation {

    private final Exception exception;
    private final Class<? extends AbstractOperation> sourceClass;

    public DataQExceptionOperation(Exception exception) {
        this.exception = exception;
        this.sourceClass = getClass();
    }

    public DataQExceptionOperation(Exception exception, Class<? extends AbstractOperation> sourceClass) {
        this.exception = exception;
        this.sourceClass = sourceClass;
    }

    @Override
    public OperationResult doExecute() {
        LoggerFactory.getLogger(sourceClass).error("执行失败", exception);
        return OperationResult.fromException(exception);
    }

}
