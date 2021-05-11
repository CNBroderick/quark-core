package org.bklab.quark.operation.jdbc;

import dataq.core.jdbc.DBAccess;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;

public interface HasJdbcConnection {

    default Connection createConnection() throws Exception {
        return createConnection(false);
    }

    default Connection createConnection(boolean onlyRead) throws Exception {
        return onlyRead ? createOnlyReadConnection() : createUpdateConnection();
    }

    Connection createUpdateConnection() throws Exception;

    default Connection createOnlyReadConnection() throws Exception {
        return createUpdateConnection();
    }

    default String createConnectionDataSourceName(boolean onlyRead) throws Exception {
        return onlyRead ? createOnlyReadConnectionDataSourceName() : createUpdateConnectionDataSourceName();
    }

    default String createUpdateConnectionDataSourceName() throws Exception {
        return "update-connection";
    }

    default String createOnlyReadConnectionDataSourceName() throws Exception {
        return "only-read-connection";
    }

    default DBAccess setDsName(DBAccess db, String name) {
        try {
            Field declaredField = DBAccess.class.getDeclaredField("dsname");
            declaredField.setAccessible(true);
            declaredField.set(db, name);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("初始化[DBAccess ds name]为[" + name + "]失败：", e);
        }
        return db;
    }

    default DBAccess createDBAccess() throws Exception {
        return createDBAccess(false);
    }

    default DBAccess createDBAccess(boolean onlyRead) throws Exception {
        return setDsName(DBAccess.fromConnection(createConnection(onlyRead)), createConnectionDataSourceName(onlyRead));
    }
}
