package org.bklab.quark.operation.jdbc;

import dataq.core.operation.AbstractOperation;
import dataq.core.operation.JdbcOperation;
import org.bklab.quark.common.dataq.core.operation.error.DataQExceptionOperation;
import org.bklab.quark.operation.internal.HasAbstractOperation;

public interface HasAbstractJdbcOperation extends HasJdbcConnection, HasAbstractOperation {

    @Override
    default AbstractOperation createAbstractOperation() {
        return createJdbcOperation();
    }

    default AbstractOperation createJdbcOperation(boolean onlyRead) {
        Class<? extends JdbcOperation> jdbcOperationClass = getJdbcOperationClass();
        try {
            JdbcOperation jdbcOperation = jdbcOperationClass.getDeclaredConstructor().newInstance();
            jdbcOperation.setOperationName(getOperationName());
            jdbcOperation.setDBAccess(createDBAccess(onlyRead));
            return jdbcOperation;
        } catch (Exception e) {
            return new DataQExceptionOperation(e, jdbcOperationClass);
        }
    }

    default AbstractOperation createJdbcOperation() {
        return createJdbcOperation(isReadonlyOperation());
    }

    default AbstractOperation createJdbcUpdateOperation() {
        return createJdbcOperation(false);
    }

    default AbstractOperation createJdbcReadOperation() {
        return createJdbcOperation(true);
    }

    Class<? extends JdbcOperation> getJdbcOperationClass();

    default String getOperationName() {
        return getJdbcOperationClass().getSimpleName();
    }

    default boolean isReadonlyOperation() {
        return false;
    }
}
