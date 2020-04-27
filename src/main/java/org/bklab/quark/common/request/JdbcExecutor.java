package org.bklab.quark.common.request;

import dataq.core.jdbc.DBAccess;
import dataq.core.operation.JdbcOperation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JdbcExecutor implements IRequestExecutor {

    private final Class<? extends JdbcOperation> JdbcOperationClass;
    private final DBAccess dbAccess;
    private final List<Consumer<JdbcOperation>> beforeExecuteListeners = new ArrayList<>();
    private final List<Consumer<JdbcOperation>> afterExecuteListeners = new ArrayList<>();
    private Class<?>[] initParameterTypes = new Class<?>[]{};
    private Object[] initArgs = new Object[]{};

    public JdbcExecutor(Class<? extends JdbcOperation> jdbcOperationClass, DBAccess dbAccess) {
        JdbcOperationClass = jdbcOperationClass;
        this.dbAccess = dbAccess;
    }

    public JdbcExecutor(Class<? extends JdbcOperation> jdbcOperationClass, DBAccess dbAccess, Class<?>[] initParameterTypes, Object[] initArgs) {
        JdbcOperationClass = jdbcOperationClass;
        this.dbAccess = dbAccess;
        this.initParameterTypes = initParameterTypes;
        this.initArgs = initArgs;
    }

    public JdbcExecutor initParameterTypes(Class<?>... parameterTypes) {
        this.initParameterTypes = parameterTypes;
        return this;
    }

    public JdbcExecutor initArgs(Object... initArgs) {
        this.initArgs = initArgs;
        return this;
    }

    @Override
    public Response execute(Request request) {
        JdbcOperation jdbcOperation = null;
        try {
            jdbcOperation = createInstance();
            request.getParams().forEach(jdbcOperation::setParam);

            callBeforeExecuteListeners(jdbcOperation);

            return new Response(jdbcOperation.execute());
        } catch (Exception e) {
            return new Response(e);
        } finally {
            callAfterExecuteListeners(jdbcOperation);
        }
    }

    public JdbcOperation createInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        JdbcOperation jdbcOperation = JdbcOperationClass.getDeclaredConstructor(initParameterTypes).newInstance(initArgs);
        jdbcOperation.setDBAccess(dbAccess).setOperationName(JdbcOperationClass.getSimpleName());
        return jdbcOperation;

    }

    public JdbcOperation createJdbcOperation() {
        try {
            return createInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JdbcExecutor addBeforeExecuteListeners(Consumer<JdbcOperation> beforeExecuteListener) {
        this.beforeExecuteListeners.add(beforeExecuteListener);
        return this;
    }

    public JdbcExecutor addAfterExecuteListeners(Consumer<JdbcOperation> afterExecuteListener) {
        this.afterExecuteListeners.add(afterExecuteListener);
        return this;
    }

    private void callBeforeExecuteListeners(JdbcOperation jdbcOperation) {
        beforeExecuteListeners.forEach(e -> e.accept(jdbcOperation));
    }

    private void callAfterExecuteListeners(JdbcOperation jdbcOperation) {
        afterExecuteListeners.forEach(e -> e.accept(jdbcOperation));
    }
}
