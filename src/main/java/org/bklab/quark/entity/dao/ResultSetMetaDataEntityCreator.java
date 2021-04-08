package org.bklab.quark.entity.dao;

import dataq.core.data.schema.Record;
import dataq.core.jdbc.DBAccess;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResultSetMetaDataEntityCreator {

    private final ResultSetMetaData metaData;
    private final Map<String, Record> columnData;
    private final String tableName;
    private final Connection connection;

    private boolean hasNoArgConstructor = false;
    private boolean hasGetterSetter = false;

    public ResultSetMetaDataEntityCreator(String tableName, Connection connection) throws Exception {
        this.tableName = tableName;
        this.connection = connection;
        this.metaData = createMetaData();
        this.columnData = createColumnData();
    }

    public ResultSetMetaDataEntityCreator hasNoArgConstructor() {
        this.hasNoArgConstructor = true;
        return this;
    }

    public ResultSetMetaDataEntityCreator hasGetterSetter() {
        this.hasGetterSetter = true;
        return this;
    }

    private ResultSetMetaData createMetaData() throws Exception {
        return connection.prepareStatement("SELECT * FROM " + tableName + " LIMIT 0;").executeQuery().getMetaData();
    }

    private Map<String, Record> createColumnData() throws Exception {
        return DBAccess.fromConnection(connection).queryForRecordset("SHOW FULL COLUMNS FROM " + tableName + ";")
                .asList().stream().collect(Collectors.toMap(r -> r.getString("Field"), Function.identity()));
    }

    private String getClassRealPath(Class<?> baseClass) {
        if (baseClass == null) baseClass = getClass();
        return Objects.requireNonNull(baseClass.getResource(""))
                .getPath().replace("/target/classes/", "/src/main/java/").substring(1);
    }

    public ResultSetMetaDataEntityCreator writePojoClass(Class<?> baseClass) throws Exception {
        Files.writeString(Path.of(getClassRealPath(baseClass) + getName(tableName, true) + ".java"),
                "package " + baseClass.getPackageName() + ";\n\n" + getPojoClass(), StandardCharsets.UTF_8);
        return this;
    }

    public ResultSetMetaDataEntityCreator writePojoClass(String directoryPath) throws Exception {
        Files.writeString(Path.of(directoryPath + getName(tableName, true) + ".java"), getPojoClass(), StandardCharsets.UTF_8);
        return this;
    }

    public ResultSetMetaDataEntityCreator writeEntityRowMapper(Class<?> baseClass) throws Exception {
        Files.writeString(Path.of(getClassRealPath(baseClass) + getName(tableName, true) + "RowMapper.java"),
                "package " + baseClass.getPackageName() + ";\n\n" + getEntityRowMapper(), StandardCharsets.UTF_8);
        return this;
    }

    public ResultSetMetaDataEntityCreator writeEntityRowMapper(String directoryPath) throws Exception {
        Files.writeString(Path.of(directoryPath + getName(tableName, true) + "RowMapper.java"), getEntityRowMapper(), StandardCharsets.UTF_8);
        return this;
    }

    public String getPojoClass() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        String space = "    ";
        String className = getName(tableName, true);
        stringBuilder.append("public class ").append(className).append(" {\n\n");

        if (hasNoArgConstructor) {
            stringBuilder.append(space).append("public ").append(className).append("() {\n")
                    .append(space).append(space).append("\n").append(space).append("}").append("\n\n");
        }

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String modifier = getModifier(i);
            String name = getName(metaData.getColumnName(i), false);
            String comment = columnData.get(metaData.getColumnName(i)).getString("Comment");
            if (comment != null && !comment.isBlank()) {
                String[] split = comment.split("\n");
                if (split.length == 1) {
                    stringBuilder.append('\n').append(space).append("/** ").append(split[0]).append(" */\n");
                } else {
                    stringBuilder.append('\n').append(space).append("/** ").append('\n');
                    for (String s : split) {
                        stringBuilder.append(space).append(" * ").append(s).append(" <br/>").append('\n');
                    }
                    stringBuilder.append(space).append(" */\n");
                }
            }


            stringBuilder.append(space).append("private ").append(modifier).append(' ').append(name).append(';').append("\n\n");
        }

        if (hasGetterSetter) {

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String modifier = getModifier(i);
                String name = getName(metaData.getColumnName(i), false);
                String upperName = getName(metaData.getColumnName(i), true);
                String comment = columnData.get(metaData.getColumnName(i)).getString("Comment");

                stringBuilder.append(space).append("public ").append(className).append(" set").append(upperName)
                        .append("(").append(modifier).append(" ").append(name).append(") {\n");
                stringBuilder.append(space).append(space).append("this.").append(name).append(" = ").append(name).append(';').append("\n");
                stringBuilder.append(space).append(space).append("return this;\n");
                stringBuilder.append(space).append("}\n\n");

                stringBuilder.append(space).append("public ").append(modifier)
                        .append(modifier.equalsIgnoreCase("boolean") ? " is" : " get")
                        .append(upperName).append("() {\n");
                stringBuilder.append(space).append(space).append("return ").append(name).append(";\n");
                stringBuilder.append(space).append("}\n\n");
            }
        }

        stringBuilder.append(space).append("@Override\n").append(space).append("public String toString() {\n")
                .append(space).append(space).append("return new GsonJsonObjectUtil(this).pretty();\n").append(space).append("}\n");

        return stringBuilder.append("}").toString();
    }

    public String getEntityRowMapper() throws Exception {

        StringBuilder stringBuilder = new StringBuilder();
        String space = "    ";
        String className = getName(tableName, true);
        stringBuilder.append("public class ").append(className).append("RowMapper")
                .append(" implements IEntityRowMapper<").append(className).append("> {\n\n");

        if (hasNoArgConstructor) {
            stringBuilder.append(space).append("public ").append(className).append("RowMapper").append("() {\n")
                    .append(space).append(space).append("\n").append(space).append("}").append("\n\n");
        }

        stringBuilder.append(space).append("@Override").append('\n');
        stringBuilder.append(space).append("public ").append(className).append(" mapRow(ResultSet r) throws Exception {").append('\n');

        stringBuilder.append(space).append(space).append("return new ").append(className).append("()").append("\n");

        for (int i = 1; i <= metaData.getColumnCount(); i++) {

            String modifier = getName(getModifier(i), true);
            String columnName = metaData.getColumnName(i);
            String name = getName(columnName, true);
            stringBuilder.append(space).append(space).append(space).append(space).append(".set").append(name);
            if (modifier.equals("LocalDateTime") || modifier.equals("LocalDate") || modifier.equals("LocalTime")) {
                stringBuilder.append("(get").append(modifier).append("(r, ").append('"').append(columnName).append('"').append("))\n");
                continue;
            }
            stringBuilder.append("(r.get").append(modifier).append('(').append('"').append(columnName).append('"').append("))\n");
        }

        stringBuilder.append(space).append(space).append(space).append(space).append(';').append("\n");
        stringBuilder.append(space).append('}').append("\n");
        return stringBuilder.append("\n}").toString();
    }

    private String getModifier(int index) throws Exception {
        int type = metaData.getColumnType(index);
        if (type == Types.TINYINT && metaData.getColumnDisplaySize(index) == 1) return "boolean";
        switch (type) {
            case Types.ARRAY:
                return "String[]";
            case Types.BIT:
                return "boolean";
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return "int";
            case Types.BIGINT:
                return "BigInteger";
            case Types.FLOAT:
                return "float";
            case Types.REAL:
            case Types.DOUBLE:
            case Types.DECIMAL:
                return "double";
            case Types.NUMERIC:
                return "Number";
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.DISTINCT:
            case Types.STRUCT:
                return "String";
            case Types.DATE:
                return "LocalDate";
            case Types.TIME:
                return "LocalTime";
            case Types.TIMESTAMP:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return "LocalDateTime";
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return "byte[]";
            case Types.NULL:
                return "Void";
            case Types.BLOB:
                return "Blob";
            case Types.CLOB:
                return "Clob";
            case Types.REF:
                return "Ref";
            case Types.NCLOB:
                return "NClob";
            case Types.SQLXML:
                return "XmlObject";
            case Types.OTHER:
            case Types.JAVA_OBJECT:
            case Types.REF_CURSOR:
            default:
                return "Object";
        }
    }

    private String getName(String dbName, boolean upperFirst) {
        List<Character> characters = new ArrayList<>();
        boolean start = !dbName.startsWith("tb_") && !dbName.startsWith("d_") && !dbName.startsWith("m_");
        char[] c = dbName.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (!start) {
                if (c[i] == '_') start = true;
                continue;
            }

            if (c[i] == '_') continue;
            if (i > 0 && c[i - 1] == '_' && (upperFirst || characters.size() > 0)) {
                characters.add(getUpperCase(c[i]));
            } else {
                characters.add(c[i]);
            }
        }
        characters.set(0, upperFirst ? getUpperCase(characters.get(0)) : getLowerCase(characters.get(0)));
        return characters.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private char getUpperCase(char c) {
        if (c >= 'a' && c <= 'z') c -= 32;
        return c;
    }

    private char getLowerCase(char c) {
        if (c >= 'A' && c <= 'Z') c += 32;
        return c;
    }
}
