package org.bklab.quark.operation.jdbc;

import dataq.core.jdbc.DBAccess;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;

public interface HasJdbcConnection {

    default Connection createConnection() {
        return createConnection(false);
    }

    default Connection createConnection(boolean onlyRead) {
        return onlyRead ? createOnlyReadConnection() : createUpdateConnection();
    }

    Connection createUpdateConnection();

    default Connection createOnlyReadConnection() {
        return createUpdateConnection();
    }

    default String createConnectionDataSourceName(boolean onlyRead) {
        return onlyRead ? createOnlyReadConnectionDataSourceName() : createUpdateConnectionDataSourceName();
    }

    default String createUpdateConnectionDataSourceName() {
        return "update-connection";
    }

    default String createOnlyReadConnectionDataSourceName() {
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

    default DBAccess createDBAccess() {
        return createDBAccess(false);
    }

    default DBAccess createDBAccess(boolean onlyRead) {
        return setDsName(DBAccess.fromConnection(createConnection(onlyRead)), createConnectionDataSourceName(onlyRead));
    }
}
