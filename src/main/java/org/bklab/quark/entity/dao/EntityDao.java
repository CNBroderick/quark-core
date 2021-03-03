package org.bklab.quark.entity.dao;

import org.bklab.quark.entity.Entity;
import org.bklab.quark.entity.EntityProperty;
import org.bklab.quark.entity.EntitySchema;
import org.bklab.quark.service.JdbcConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
public class EntityDao implements Supplier<Entity[]> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Entity[] entities;
    private Connection connection = getConnection();

    public EntityDao(Entity... entities) {
        if (entities == null || entities.length == 0)
            throw new IllegalArgumentException("构造函数参数Entity为空");
        this.entities = entities;
    }

    public EntityDao(Collection<Entity> entities) {
        if (entities == null || entities.size() == 0)
            throw new IllegalArgumentException("构造函数参数Entity为空");
        this.entities = entities.toArray(new Entity[]{});
    }

    public EntityDao nextEntityInstanceId() {
        String addSql = "UPDATE tb_instance_id SET d_id = d_id + " + entities.length + " WHERE d_name = 'entity';";
        String getSql = "SELECT d_id FROM tb_instance_id WHERE d_name = 'entity';";
        String sql = "INSERT INTO tb_entity_id(d_name, d_create_time) VALUES (?, NOW())";
        try {
            Connection connection = getConnection();
            Savepoint savepoint = connection.setSavepoint();
            PreparedStatement statement = this.connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            for (Entity entity : entities) {
                statement.setString(1, entity.getName());
                statement.addBatch();
            }
            statement.executeBatch();

            Long id = new ResultSetHelper(statement.getGeneratedKeys()).asLong();
            if (id != null) {
                for (Entity entity : entities) {
                    entity.setEntityInstanceId(id++);
                }
            }



            long a = new PreparedStatementHelper(this.connection.prepareStatement(addSql)).executeUpdate();
            if (a < 1) {
                insertInitToTableTbInstanceIdAndSet();
                return this;
            }
            id = new PreparedStatementHelper(this.connection.prepareStatement(getSql)).executeQuery().asLong();
            assert id > 0;
            for (int i = 0; i < entities.length; i++) {
                entities[i].setEntityInstanceId(id - entities.length + i);
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.contains("doesn't exist") && message.contains("tb_instance_id")) {
                createTableTbInstanceId();
                insertInitToTableTbInstanceIdAndSet();
            } else {
                logger.error("获取nextEntityInstanceId时发生异常。", e);
            }
        }

        return this;
    }

    private void createTableTbInstanceId() {
        String ddl = "CREATE TABLE IF NOT EXISTS `tb_entity_id` " +
                "       ( " +
                "           `d_id`   int(64)  auto_increment primary key not null, " +
                "           `d_name` varchar(64)            not null, " +
                "           `d_create_time`   datetime default NOW() not null, " +
                "           constraint tb_instance_id_u_index unique (d_id) " +
                "       ) ENGINE = InnoDB " +
                "         DEFAULT CHARSET = utf8mb4 " +
                "         COLLATE = utf8mb4_unicode_ci " +
                "         ROW_FORMAT = DYNAMIC;";
        ddl = ddl.replaceAll(" +", " ");
        try {
            new PreparedStatementHelper(getConnection().prepareStatement(ddl)).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("无法创建 tb_instance_id。", e);
        }
    }

    private void insertInitToTableTbInstanceIdAndSet() {
        String insert = "INSERT IGNORE INTO tb_instance_id(d_name, d_id) VALUES ('entity', " + Math.max(entities.length, 1) + ");";
        String query = "SELECT d_id FROM tb_instance_id WHERE d_name = 'entity';";
        try {
            new PreparedStatementHelper(getConnection().prepareStatement(insert)).executeUpdate();
            long id = new PreparedStatementHelper(getConnection().prepareStatement(query)).executeQuery().asLong();
            assert id > 0;
            for (int i = 0; i < entities.length; i++) {
                entities[i].setEntityInstanceId(id - entities.length + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("初始化 tb_instance_id 数据失败。", e);
        }
    }

    public EntityDao createTables(boolean isBeforeDropTable) {
        Connection connection = getConnection();
        Savepoint savepoint = null;
        try {
            savepoint = connection.setSavepoint();
            if (isBeforeDropTable) {
                for (String ddl : generateDropTableDdl()) {
                    new PreparedStatementHelper(connection.prepareStatement(ddl)).executeUpdate();
                }
            }
            for (String ddl : generateCreateTableDdl()) {
                new PreparedStatementHelper(connection.prepareStatement(ddl)).executeUpdate();
            }
            connection.commit();
            connection.releaseSavepoint(savepoint);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("初始化 tb_instance_id 数据失败。", e);
            if (savepoint != null) rollback(savepoint);
        }
        return this;
    }

    public String[] generateDropTableDdl() {
        Set<String> strings = new HashSet<>();
        for (Entity entity : entities) {
            strings.add("DROP TABLE IF EXISTS `tb_" + entity.getSchema().getTableName() + "`; ");
        }
        return strings.toArray(new String[]{});
    }

    public String[] generateCreateTableDdl() {
        Set<String> strings = new HashSet<>();
        for (Entity entity : entities) {
            strings.add(generateCreateTableDdl(entity.getSchema()));
        }
        return strings.toArray(new String[]{});
    }

    public String generateCreateTableDdl(EntitySchema entitySchema) {
        String tableName = entitySchema.getTableName();
        StringBuilder sql = new StringBuilder("create table `" + tableName + "` ( ");

        Set<String> primaries = new LinkedHashSet<>();
        Set<String> uniques = new LinkedHashSet<>();
        for (EntityProperty<?> property : entitySchema.getProperties().values()) {
            String fieldName = property.getFieldName();
            StringBuilder row = new StringBuilder("`" + fieldName + "` "
                    + Objects.requireNonNull(property.getFieldType(),
                    "无法自动生成field-type，请在schema-->property中指定。"));

            if (property.isPrimary()) {
                primaries.add(fieldName);
            }

            if (property.isNotnull()) {
                row.append(" not null");
            }

            if (property.isAutoIncrease()) {
                row.append(" auto_increment");
            }

            if (property.isUnique()) {
                uniques.add(fieldName);
            }

            if (property.getFieldDefault() != null) {
                row.append(" default ").append(property.getFieldDefault());
            } else if (property.getDefaultValue() != null) {
                if (Number.class.isAssignableFrom(property.getType()))
                    row.append(" default ").append(property.getDefaultValue());
                if (CharSequence.class.isAssignableFrom(property.getType()))
                    row.append(" default '").append(property.getDefaultValue()).append('\'');
                if (Temporal.class.isAssignableFrom(property.getType()))
                    row.append(" default '").append(property.getDefaultValue()).append('\'');
            }

            if (property.getCaption() != null) {
                row.append(" comment '").append(property.getCaption()).append("'");
            }
            row.append(", ");
            sql.append(row);
        }

        if (!primaries.isEmpty() || !uniques.isEmpty()) {
            sql.append("constraint `").append(tableName).append("_pk` ");
        }

        if (!primaries.isEmpty()) {
            sql.append(" PRIMARY KEY (`").append(String.join("`, `", primaries)).append("`),");
        }

        if (!uniques.isEmpty()) {
            sql.append(" UNIQUE KEY (`").append(String.join("`, `", uniques)).append("`),");
        }

        if (sql.toString().endsWith(",")) {
            sql.deleteCharAt(sql.length() - 1);
        }

        sql.append(") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;");

        return sql.toString().replaceAll(" +", " ");
    }

    public void insert() throws Exception {
        nextEntityInstanceId();
        Connection connection = getConnection();
        Savepoint savepoint = connection.setSavepoint();
        try {
            for (Map.Entry<String, List<Entity>> entry : generateInsertDml().entrySet()) {
                boolean returnGeneratedKeys = entry.getValue().stream().anyMatch(e -> e.getSchema().hasAutoIncrease());
                PreparedStatement statement = connection.prepareStatement(entry.getKey(),
                        returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);

                String errors = checkErrors(entry.getValue());
                if (errors != null) throw new EntityDaoException(errors);

                for (Entity entity : entry.getValue()) {
                    int i = 1;
                    for (EntityProperty<?> property : entity.properties()) {
                        if (property.isAutoIncrease()) continue;
                        statement.setObject(i++, property.toSaveValue());
                    }
                    statement.addBatch();
                }
                statement.executeBatch();

                if (returnGeneratedKeys) {
                    Long id = new ResultSetHelper(statement.getGeneratedKeys()).asLong();
                    if (id != null) {
                        for (Entity entity : entry.getValue()) {
                            entity.getSchema().setAutoIncreaseValue(id++);
                        }
                    }
                }

                statement.close();
            }
            connection.commit();
            connection.releaseSavepoint(savepoint);
        } catch (Exception e) {
            throw new EntityDaoException("保存时发生错误：", e);
        } finally {
            rollback(savepoint);
        }
    }

    private String checkErrors(Collection<Entity> entities) {
        StringBuilder b = new StringBuilder();
        for (Entity entity : entities) {
            String a = printValidateErrors(entity, validate(entity));
            if (!a.isBlank()) b.append(a);
        }
        String s = b.toString();
        return s.isBlank() ? null : s;
    }

    private String printValidateErrors(Entity entity, Collection<String> errors) {
        if (errors == null || errors.isEmpty()) return "";
        String[] array = errors.toArray(new String[]{});
        StringBuilder e = new StringBuilder(String.format("\n%s - EntityInstanceId=%d, 包含%d个错误:\n", entity.getName(), entity.getEntityInstanceId(), array.length));
        for (int i = 1; i <= array.length; i++) {
            e.append("\t").append(i).append(". ").append(array[i - 1]).append("\n");
        }
        return e.toString();
    }

    private List<String> validate(Entity entity) {
        return entity.properties().stream().map(EntityProperty::validate).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Map<String, List<Entity>> generateInsertDml() {
        return Stream.of(entities)
                .collect(Collectors.groupingBy(entity -> generateInsertDml(entity.getSchema()),
                        Collectors.mapping(Function.identity(), Collectors.toList())));
    }

    public String generateInsertDml(EntitySchema entitySchema) {
        StringBuilder b = new StringBuilder("INSERT INTO ");
        b.append('`').append(entitySchema.getTableName()).append("` (");

        List<String> fields = new ArrayList<>();
        List<String> questionMarks = new ArrayList<>();

        for (EntityProperty<?> property : entitySchema.getProperties().values()) {
            if (property.isAutoIncrease()) continue;
            fields.add(property.getFieldName());
            questionMarks.add(property.getFieldDefault() == null ? "?" : "IFNULL(?, " + property.getFieldDefault() + ")");
        }

        if (fields.isEmpty()) {
            return b.append(") VALUES ();").toString();
        }

        b.append("`").append(String.join("`, `", fields)).append("`) VALUES (");
        b.append(String.join(",", questionMarks)).append(");");
        return b.toString();
    }

    public String generateSelectDql(EntitySchema schema) {
        return "SELECT * FROM " + schema.getTableName();
    }

    public String generateSelectDql(EntitySchema schema, Map<String, ?> map) {
        if (map == null || map.isEmpty()) return generateSelectDql(schema);
        StringBuilder b = new StringBuilder("SELECT * FROM " + schema.getTableName() + "WHERE");

        // TODO: 2020-04-30
        return null;
    }

    private Connection getConnection() {
        try {
            return connection == null || connection.isClosed() ? JdbcConnectionManager.getConnection() : connection;
        } catch (SQLException e) {
            this.connection = JdbcConnectionManager.getConnection();
            if (connection == null) throw new RuntimeException("无法获取数据库连接。");
            return connection;
        }
    }

    private void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            logger.error("关闭connection、statements失败。", e);
        }
    }

    private void rollback(Savepoint savePoint) {
        try {
            if (connection != null && !connection.isClosed()) {
                if (savePoint != null) connection.rollback(savePoint);
                else connection.rollback();
            }
        } catch (SQLException e) {
            logger.error("关闭connection、statements失败。", e);

        }
    }

    @Override
    public Entity[] get() {
        return entities;
    }


    private static class EntityDaoException extends RuntimeException {

        public EntityDaoException(String message) {
            super(message);
        }

        public EntityDaoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
