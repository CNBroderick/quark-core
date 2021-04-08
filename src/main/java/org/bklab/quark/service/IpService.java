package org.bklab.quark.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysql.cj.jdbc.MysqlDataSource;
import dataq.core.operation.OperationResult;
import org.bklab.quark.util.location.LocationUtil;
import org.bklab.quark.util.security.SM4Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class IpService implements Service<IpService> {
    private static final String a = "^10\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$";
    private static final String b = "^172\\.(1[6789]|2[0-9]|3[01])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$";
    private static final String c = "^192\\.168\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$";
    private static final IpService instance = new IpService();
    private final List<Consumer<Exception>> exceptionConsumers = new ArrayList<>();
    private final List<Consumer<OperationResult>> saveListeners = new ArrayList<>();
    private final Map<String, String> data = new ConcurrentHashMap<>();
    private final Connection connection = createConnection();

    private IpService() {

    }

    public static IpService getInstance() {
        return instance;
    }

    private Connection createConnection() {
        try {

            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setUseSSL(true);

            mysqlDataSource.setCharacterEncoding("UTF-8");
            mysqlDataSource.setCharacterSetResults("UTF-8");

            mysqlDataSource.setServerName("db.bbkki.com");
            mysqlDataSource.setPort(3306);
            mysqlDataSource.setUser(SM4Util.decode("cf405888abed553d3979f0884524bbcf", "d6d208e76f3d930e18705144b0d573c3"));
            mysqlDataSource.setPassword(SM4Util.decode("f0704b38be68321406d8117dfd6b9b0b", "d6d208e76f3d930e18705144b0d573c3"));
            mysqlDataSource.setDatabaseName("ip");
            mysqlDataSource.setAutoReconnect(true);

            return mysqlDataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getCity(String ip) {
        String city = isDetectionIntranet(ip);
        return city != null ? city : data.containsKey(ip) ? data.get(ip) : getCityFromDb(ip);
    }

    public boolean isInRule(List<String> whitelist, String ip) {
        if (whitelist == null || whitelist.isEmpty() || ip == null || ip.trim().isEmpty()) return true;
        String detectionIntranet = isDetectionIntranet(ip);
        if (detectionIntranet != null) {
            return whitelist.stream().anyMatch(detectionIntranet::contains);
        }
        if (connection == null) return false;
        String sql = "SELECT concat(`country`,',',`region`,',',`city`) as address FROM `internet_protocol` WHERE `ip` = ? LIMIT 1;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, ip);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String addressForDb = resultSet.getString(0);
                return whitelist.stream().anyMatch(addressForDb::contains);
            }
            JsonObject o = LocationUtil.getAddressFromIp(ip);
            if (o != null && !o.get("city").isJsonArray()) {
                String addressForAMap = o.get("city").getAsString() + ","
                        + o.get("province").getAsString() + ","
                        + (o.get("city").getAsString() == null ? "" : "中国");
                return whitelist.stream().anyMatch(addressForAMap::contains);
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private String isDetectionIntranet(String ip) {
        if (Arrays.asList("127.0.0.1", "localhost", ":::", ":::1", "0:0:0:0:0:0:0:1").contains(ip)) return "本地环回";
        if ("255.255.255.255".equals(ip)) return "广播地址";
        if ("0.0.0.0".equals(ip)) return "元地址";
        if (Pattern.matches(a, ip)) return "A类内网";
        if (Pattern.matches(b, ip)) return "B类内网";
        if (Pattern.matches(c, ip)) return "C类内网";
        return null;
    }

    private String getCityFromAMap(String ip) {
        try {
            JsonObject o = LocationUtil.getAddressFromIp(ip);
            if (o != null) {
                String city = Optional.ofNullable(o.get("city")).filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsString).orElse(null);
                String province = Optional.ofNullable(o.get("province")).filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsString).orElse(null);
                String country = city == null ? "" : "中国";
                if (city == null) return ip;
                new Thread(() -> save(ip, country, province, city)).start();
                return city;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    private String getCityFromDb(String ip) {
        if (connection == null) return getCityFromAMap(ip);
        try {
            String sql = "SELECT `city` FROM `internet_protocol` WHERE `ip` = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, ip);
            ResultSet set = statement.executeQuery();
            if (set.first()) {
                addQueryCount(ip);
                String city = set.getString(1);
                data.put(ip, city);
                return set.getString(1);
            } else {
                return getCityFromAMap(ip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    private void addQueryCount(String ip) {
        new Thread(() -> {
            try {
                if (connection == null) return;
                PreparedStatement statement = connection.prepareStatement("UPDATE `internet_protocol` SET `query_count` = `query_count` + 1 WHERE `ip` = ?;");
                statement.setString(1, ip);
                statement.execute();
            } catch (Exception ignore) {
            }
        }).start();
    }

    protected void save(String ip, String country, String region, String city) {
        if (ip == null || city == null) return;
        data.put(ip, city);
        String sql = "INSERT INTO `internet_protocol`(`ip` , `country`, `region`, `city`) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `country` = ?, `region` = ?, `city` = ?, `update_time` = ?;";
        if (connection == null) return;
        try {

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, ip);
            statement.setString(2, country);
            statement.setString(3, region);
            statement.setString(4, city);
            statement.setString(5, country);
            statement.setString(6, region);
            statement.setString(7, city);
            statement.setObject(8, LocalDateTime.now());
            statement.execute();
        } catch (Exception e) {
            callExceptionConsumers(e);
        }
    }

    @Override
    public String getCurrentUserId() {
        return "Broderick Labs";
    }

    @Override
    public List<Consumer<Exception>> getExceptionConsumers() {
        return exceptionConsumers;
    }

    @Override
    public List<Consumer<OperationResult>> getSaveListeners() {
        return saveListeners;
    }
}
