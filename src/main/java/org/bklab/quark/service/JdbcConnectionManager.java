package org.bklab.quark.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bklab.quark.util.SM4Util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;

public class JdbcConnectionManager {

    private static final JdbcConnectionManager instance = new JdbcConnectionManager();

    private final DataSource dataSource;

    private JdbcConnectionManager() {
        try {
            this.dataSource = createDruidDataSource();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static JdbcConnectionManager getInstance() {
        return instance;
    }

    public static Connection getConnection() {
        return instance.create();
    }

    public Connection create() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private DruidDataSource createDruidDataSource() {
        try {

            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setUrl("jdbc:mysql://db.bbkki.com:3306/bklab");
            druidDataSource.setName("Broderick-RDS-Beijing");


            druidDataSource.setUsername(SM4Util.decode("4b986c10670dc98e37c2050bec75e117", "cf405888abed553d3979f0884524bbcf"));
            druidDataSource.setPassword(SM4Util.decode("9d48cebcab04f9c598c48e53d3e56aab", "f0704b38be68321406d8117dfd6b9b0b"));
            druidDataSource.setDriver(new Driver());

            druidDataSource.setDefaultAutoCommit(false);

            Properties properties = new Properties();
            properties.put("jdbc.useSSL", "true");
            properties.put("jdbc.requireSSL", "true");
            properties.put("jdbc.useUnicode", "yes");
            properties.put("jdbc.characterEncoding", "UTF-8");
            properties.put("jdbc.characterSetResults", "UTF-8");
            properties.put("jdbc.character_set_server", "UTF-8");
            properties.put("jdbc.serverTimezone", "Asia/Shanghai");
            properties.put("jdbc.clientCertificateKeyStoreType", "jks");
            properties.put("jdbc.clientCertificateKeyStoreUrl", "file:" + Objects.requireNonNull(getClass().getClassLoader().getResource("cert/ApsaraDB-CA-Chain.jks")).getPath());
            properties.put("jdbc.clientCertificateKeyStorePassword", "apsaradb");
//            properties.put("config.decrypt", "true");
//            properties.put("config.decrypt.key", "${jdbc.publicKey}");
//            properties.put("jdbc.password", "S+Q0pgUKTiNDXaBtmsR2frSkjtfX62/IyCYyTJ2eVZZWr0iEXtjghSDs6xMn5AB2fJHKdNCX+7IdFi1TwBLtWA==");
//            properties.put("jdbc.publicKey", "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIaMwITuQH1++gKE7ts19hRcC7qrdf2fsz7TwGTzBeG3xyQv3Xlh9Dfyo5YWXbnfELjYN0/IHM7r+MND7/U/doMCAwEAAQ==");

            druidDataSource.setConnectProperties(properties);

            /*----下面的具体配置参数自己根据项目情况进行调整----*/
            druidDataSource.setMaxActive(20);
            druidDataSource.setInitialSize(8);
            druidDataSource.setMinIdle(1);
            druidDataSource.setMaxWait(60000);

            druidDataSource.setValidationQuery("SHOW TABLES");

            druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
            druidDataSource.setMinEvictableIdleTimeMillis(300000);

            druidDataSource.setTestWhileIdle(true);
            druidDataSource.setTestOnBorrow(false);
            druidDataSource.setTestOnReturn(false);

            druidDataSource.setPoolPreparedStatements(true);
            druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

            druidDataSource.init();
            return druidDataSource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private DataSource createMysqlDataSource() throws Exception {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUseSSL(true);
        mysqlDataSource.setRequireSSL(true);

        mysqlDataSource.setClientCertificateKeyStoreType("jks");
        mysqlDataSource.setClientCertificateKeyStoreUrl("file:" + Objects.requireNonNull(getClass().getClassLoader().getResource("cert/ApsaraDB-CA-Chain.jks")).getPath());

        mysqlDataSource.setClientCertificateKeyStorePassword("apsaradb");
        mysqlDataSource.setCharacterEncoding("UTF-8");
        mysqlDataSource.setCharacterSetResults("UTF-8");

        mysqlDataSource.setServerName("db.bbkki.com");
        mysqlDataSource.setPort(3306);
        mysqlDataSource.setUser(SM4Util.decode("4b986c10670dc98e37c2050bec75e117", "cf405888abed553d3979f0884524bbcf"));
        mysqlDataSource.setPassword(SM4Util.decode("9d48cebcab04f9c598c48e53d3e56aab", "f0704b38be68321406d8117dfd6b9b0b"));
        mysqlDataSource.setDatabaseName("bklab");
        mysqlDataSource.setAutoReconnect(true);


        return mysqlDataSource;
    }

}
