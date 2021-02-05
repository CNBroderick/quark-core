package org.bklab.quark.element;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface HasPreparedStatement {

    /**
     * @param preparedStatement @See java.sql.PreparedStatement
     * @param parameters        java.sql.PreparedStatement parameters
     * @throws SQLException java.sql.SQLException
     */
    default void insertPsParameter(PreparedStatement preparedStatement, Object... parameters) throws SQLException {
        insertPsParameter(1, preparedStatement, parameters);
    }

    /**
     * @param initPosition      should be >= 1
     * @param preparedStatement @See java.sql.PreparedStatement
     * @param parameters        java.sql.PreparedStatement parameters
     * @throws SQLException java.sql.SQLException
     */
    default void insertPsParameter(int initPosition, PreparedStatement preparedStatement, Object... parameters) throws SQLException {
        if (preparedStatement == null || parameters == null || parameters.length < 1) return;
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            int column = i + initPosition;

            if (parameter == null) {
                preparedStatement.setObject(column, null);
                continue;
            }

            if (parameter instanceof Number) {
                if (parameter instanceof Double) {
                    preparedStatement.setDouble(column, (Double) parameter);
                } else if (parameter instanceof Integer) {
                    preparedStatement.setInt(column, (Integer) parameter);
                } else if (parameter instanceof Long) {
                    preparedStatement.setLong(column, (Long) parameter);
                } else if (parameter instanceof Float) {
                    preparedStatement.setFloat(column, (Float) parameter);
                } else {
                    preparedStatement.setBigDecimal(column, new BigDecimal(parameter.toString()));
                }
                continue;
            }

            if (parameter.getClass().isEnum()) {
                preparedStatement.setString(column, ((Enum<?>) parameter).name());
                continue;
            }

            preparedStatement.setObject(column, parameter);
        }
    }

    static HasPreparedStatement get() {
        return Impl.instance;
    }

    class Impl implements HasPreparedStatement {
        public static Impl instance = new Impl();
    }
}
