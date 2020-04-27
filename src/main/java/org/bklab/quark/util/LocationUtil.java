/*
 * Class: org.bklab.util.LocationUtil
 * Modify date: 2020/3/20 上午10:25
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataq.core.httpclient.HttpClient;
import dataq.core.httpclient.HttpGetClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocationUtil {

    private static final String BRODERICK_LAB_A_MAP_KEY = "79358dac22d76a984bda9d07b080f20b";

    public static JsonObject getAddressFromIp(String ip) {
        String url = "https://restapi.amap.com/v3/ip?key=" + BRODERICK_LAB_A_MAP_KEY + "&ip=" + ip;
        HttpClient client = new HttpGetClient(url);
        try {
            ResponseBody response = client.doRequest().body();
            if (response == null) return null;
            return new Gson().fromJson(response.string(), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LocateResult> query(String keyword, String ip, String city) throws Exception {

        String url = "https:////restapi.amap.com/v3/assistant/inputtips" +
                "?key=" + BRODERICK_LAB_A_MAP_KEY +
                "&keywords=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                "&type=";
        if (ip != null) {
            String res = queryAddressFromIp(ip);
            if (res.isEmpty()) {
                if (city != null) {
                    url += "&city=" + URLEncoder.encode(city, StandardCharsets.UTF_8);
                }
            } else {
                url += "&location=" + res.split(";")[0];
                url += "&city=" + res.split(";")[1];
            }
        } else if (city != null)
            url += "&city=" + URLEncoder.encode(city, StandardCharsets.UTF_8);

        url += "&datatype=all";

        HttpClient client = new HttpGetClient(url);
        Response response = client.doRequest();
        if (response.body() != null && "OK".equals(response.message())) {
            return LocateResult.parse(response.body().string());
        }
        return new ArrayList<>();
    }

    private String queryAddressFromIp(String ip) {
        String ipUrl = "https://restapi.amap.com/v3/ip?ip=" + ip + "&output=json&key=" + BRODERICK_LAB_A_MAP_KEY;
        HttpClient client = new HttpGetClient(ipUrl);
        try {
            Response response = client.doRequest();
            if (response.body() != null && "OK".equals(response.message())) {
                JsonObject ipJson = new Gson().fromJson(response.body().string(), JsonObject.class);
                return (Float.parseFloat(ipJson.get("rectangle").getAsString().split(";")[0].split(",")[0])
                        + Float.parseFloat(ipJson.get("rectangle").getAsString().split(";")[1].split(",")[0])) / 2
                        + "," + (Float.parseFloat(ipJson.get("rectangle").getAsString().split(";")[0].split(",")[1])
                        + Float.parseFloat(ipJson.get("rectangle").getAsString().split(";")[1].split(",")[1])) / 2
                        + ';' + ipJson.get("adcode").getAsString();

            }
        } catch (Exception ignore) {
        }

        return "";
    }

    public String queryGeo(String address, String city) throws Exception {
        String url = "https://restapi.amap.com/v3/geocode/geo?key=" + BRODERICK_LAB_A_MAP_KEY + "&address=" + address;
        if (city != null)
            url += "&city=" + city;
        HttpClient client = new HttpGetClient(url);
        ResponseBody response = client.doRequest().body();
        if (response == null) return "";

        JsonArray array = new Gson().fromJson(response.string(), JsonObject.class).get("geocodes").getAsJsonArray();
        if (array.size() > 0) {
            JsonObject o = array.get(0).getAsJsonObject();
            return o.get("province").getAsString() + ","
                    + o.get("city").getAsString() + ","
                    + o.get("district").getAsString();
        }
        return "";
    }

    public static class LocateResult {
        private String id;
        private String name;
        private String district;
        private String adCode;
        private String longitude;
        private String latitude;
        private String address;
        private String typeCode;
        private String city;

        public static List<LocateResult> parse(String json) {
            Debug.info(json);
            JsonObject o = new Gson().fromJson(json, JsonObject.class);
            JsonArray array = o.getAsJsonArray("tips");
            if (array.size() < 1) return new ArrayList<>();


            return IntStream.range(1, array.size()).mapToObj(array::get).map(JsonElement::getAsJsonObject)
                    .map(tip -> {
                        String lon = "";
                        String lat = "";
                        String location = tip.get("location").getAsString();
                        if (location.length() > 0) {
                            lon = location.split(",")[0];
                            lat = location.split(",")[1];
                        }
                        return new LocateResult()
                                .setId(getString(tip, "id"))
                                .setName(getString(tip, "name"))
                                .setDistrict(getString(tip, "district"))
                                .setAdCode(getString(tip, "adcode"))
                                .setLongitude(lon)
                                .setLatitude(lat)
                                .setAddress(getString(tip, "address"))
                                .setTypeCode(getString(tip, "typecode"))
                                .setCity(o.get("city") == null
                                        ? "" : o.get("city").getAsJsonArray().size() <= 0
                                        ? "" : o.get("city").getAsJsonArray().get(0).getAsString());
                    }).collect(Collectors.toList());
        }

        private static String getString(JsonObject o, String filedName) {
            return o.get(filedName).toString().isEmpty() ? "" : o.get(filedName).getAsString();
        }

        public String getId() {
            return id;
        }

        public LocateResult setId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public LocateResult setName(String name) {
            this.name = name;
            return this;
        }

        public String getDistrict() {
            return district;
        }

        public LocateResult setDistrict(String district) {
            this.district = district;
            return this;
        }

        public String getAdCode() {
            return adCode;
        }

        public LocateResult setAdCode(String adCode) {
            this.adCode = adCode;
            return this;
        }

        public String getLongitude() {
            return longitude;
        }

        public LocateResult setLongitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public String getLatitude() {
            return latitude;
        }

        public LocateResult setLatitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public String getAddress() {
            return address;
        }

        public LocateResult setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public LocateResult setTypeCode(String typeCode) {
            this.typeCode = typeCode;
            return this;
        }

        public String getCity() {
            return city;
        }

        public LocateResult setCity(String city) {
            this.city = city;
            return this;
        }

        public String getMainInfo() {
            return String.format("%s[%s\t%s]", name, address, city);
        }

        @Override
        public String toString() {
            return "LocateResult{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", district='" + district + '\'' +
                    ", adCode='" + adCode + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", address='" + address + '\'' +
                    ", typeCode='" + typeCode + '\'' +
                    ", city='" + city + '\'' +
                    '}';
        }
    }
}
