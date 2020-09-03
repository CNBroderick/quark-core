package org.bklab.quark.entity.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrepareStatementHelper {

    private final Logger logger;
    private final PreparedStatement statement;

    public PrepareStatementHelper(PreparedStatement statement) {
        this.statement = statement;
        Class<? extends StackTraceElement> aClass = (new Throwable()).getStackTrace()[2].getClass();
        this.logger = LoggerFactory.getLogger(aClass == null ? getClass() : aClass);
    }


    public int executeUpdate(Object... parameters) throws SQLException {
        return executeUpdate(true, parameters);
    }

    public int executeUpdate(boolean commit, Object... parameters) throws SQLException {
        try {
            insertParameters(parameters);
            int a = statement.executeUpdate();
            if (commit) statement.getConnection().commit();
            return a;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("执行更新失败", e);
            throw e;
        } finally {
            close();
        }
    }

    public ResultSetHelper executeQuery(Object... parameters) throws Exception {
        try {
            insertParameters(parameters);
            return new ResultSetHelper(statement.executeQuery());
        } catch (SQLException e) {
            logger.error("执行查询失败", e);
            throw e;
        }
    }

    private void insertParameters(Object... parameters) throws SQLException {
        if (parameters == null) return;
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter instanceof Integer) statement.setInt(i + 1, (Integer) parameter);
            else if (parameter instanceof Long) statement.setLong(i + 1, (Long) parameter);
            else if (parameter instanceof Double) statement.setDouble(i + 1, (Double) parameter);
            else if (parameter instanceof Float) statement.setFloat(i + 1, (Float) parameter);
            else statement.setObject(i + 1, parameter);
        }
    }

    public void close() throws SQLException {
        try {
            if (statement != null && !statement.isClosed()) statement.close();
        } catch (SQLException e) {
            logger.error("关闭statements、resultSet失败。", e);
            throw e;
        }
    }

}
