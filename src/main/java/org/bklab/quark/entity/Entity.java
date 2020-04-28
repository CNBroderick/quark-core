package org.bklab.quark.entity;

import org.bklab.quark.service.JdbcConnectionManager;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Entity implements Serializable {

    private final EntitySchema schema;
    private final String name;
    private long entityInstanceId;
    private Connection connection;

    public Entity(EntitySchema schema) {
        this.schema = schema;
        this.name = schema.getName();
        this.entityInstanceId = schema.getEntityInstanceId();
    }

    public long getEntityInstanceId() {
        return entityInstanceId;
    }

    public Entity setEntityInstanceId(long entityInstanceId) {
        this.entityInstanceId = entityInstanceId;
        this.schema.setValue("entityInstanceId", entityInstanceId);
        return this;
    }

    public Entity nextEntityInstanceId() {
        try {
            Connection connection = getConnection();

            execute(connection.prepareStatement("UPDATE tb_instance_id SET d_id = d_id + 1 WHERE d_name = 'entity';"));
            connection.commit();
            PreparedStatement statement = connection.prepareStatement("SELECT d_id FROM tb_instance_id WHERE d_name = 'entity';");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) setEntityInstanceId(resultSet.getLong("d_id"));
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    private void execute(PreparedStatement statement, Object... parameters) throws SQLException {
        execute(true, statement, parameters);
    }

    private void execute(boolean commit, PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
        statement.execute();
        statement.close();
    }

    private Connection getConnection() {
        return JdbcConnectionManager.getConnection();
    }


}
