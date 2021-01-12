package org.bklab.quark.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataq.core.operation.OperationContext;
import org.bklab.quark.util.time.LocalDateTimeFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class JsonObjectInjector {

    private final JsonObject jsonObject;
    private final OperationContext context;
    private final int initSize;

    public JsonObjectInjector(JsonObject jsonObject, OperationContext context) {
        this.jsonObject = jsonObject;
        this.context = context;
        this.initSize = jsonObject.size();
    }

    public JsonObjectInjector(JsonObject jsonObject, OperationContext context, int initSize) {
        this.jsonObject = jsonObject;
        this.context = context;
        this.initSize = initSize;
    }

    public JsonObjectInjector number(String parameterName, String requestName) {
        Optional.ofNullable(getContext().<Number>getObject(parameterName))
                .ifPresent(number -> jsonObject.addProperty(requestName, number));
        return this;
    }

    /**
     * @param names String parameterName, String requestName 循环个数必须为双数
     */
    public JsonObjectInjector numbers(String... names) {
        checkEvenNumbers(names);
        for (int i = 0; i + 1 < names.length; i++) number(names[i], names[i + 1]);
        return this;
    }

    public JsonObjectInjector string(String parameterName, String requestName) {
        Optional.ofNullable(getContext().<String>getObject(parameterName))
                .ifPresent(string -> jsonObject.addProperty(requestName, string));
        return this;
    }

    /**
     * @param names String parameterName, String requestName 循环个数必须为双数
     */
    public JsonObjectInjector strings(String... names) {
        checkEvenNumbers(names);
        for (int i = 0; i  + 1 < names.length; i++) string(names[i], names[i + 1]);
        return this;
    }

    public JsonObjectInjector injectBoolean(String parameterName, String requestName) {
        Optional.ofNullable(getContext().<Boolean>getObject(parameterName))
                .ifPresent(value -> jsonObject.addProperty(requestName, value));
        return this;
    }

    /**
     * @param names String parameterName, String requestName 循环个数必须为双数
     */
    public JsonObjectInjector injectBooleans(String... names) {
        return booleans(names);
    }

    public JsonObjectInjector booleans(String... names) {
        checkEvenNumbers(names);
        checkEvenNumbers(names);
        for (int i = 0; i  + 1 < names.length; i++) injectBoolean(names[i], names[i + 1]);
        return this;
    }

    private void checkEvenNumbers(String... names) {
        if (names.length % 2 > 0) throw new RuntimeException("inject parameter names must is a even numbers.");
    }

    public JsonObjectInjector datetime(String parameterName, String requestName) {
        Optional.ofNullable(getContext().<LocalDateTime>getObject(parameterName))
                .ifPresent(value -> jsonObject.addProperty(requestName, LocalDateTimeFormatter.Short(value)));
        return this;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public JsonObjectInjector datetimes(String... names) {
        checkEvenNumbers(names);
        for (int i = 0; i  + 1 < names.length; i++) datetime(names[i], names[i + 1]);
        return this;
    }

    public JsonObjectInjector time(String parameterName, String requestName) {
        Optional.ofNullable(getContext().<LocalTime>getObject(parameterName))
                .ifPresent(value -> jsonObject.addProperty(requestName, LocalDateTimeFormatter.Short(value)));
        return this;
    }

    public JsonObjectInjector times(String... names) {
        checkEvenNumbers(names);
        for (int i = 0; i  + 1 < names.length; i++) time(names[i], names[i + 1]);
        return this;
    }

    public JsonObjectInjector date(String parameterName, String requestName) {
        Optional.ofNullable(getContext().<LocalDate>getObject(parameterName))
                .ifPresent(value -> jsonObject.addProperty(requestName, LocalDateTimeFormatter.Short(value)));
        return this;
    }

    public JsonObjectInjector dates(String... names) {
        checkEvenNumbers(names);
        for (int i = 0; i + 1 < names.length; i++) date(names[i], names[i + 1]);
        return this;
    }

    public <E> JsonObjectInjector array(String parameterName, String requestName, Function<E, JsonElement> function) {
        Optional.ofNullable(getContext().<E>getObject(parameterName))
                .ifPresent(s -> jsonObject.add(requestName, function.apply(s)));
        return this;
    }

    public JsonObjectInjector arrayString(String parameterName, String requestName) {
        return this.<Collection<String>>array(parameterName, requestName, s ->
                s.stream().collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
    }

    /**
     * 是否开启过滤器
     *
     * @return this
     */
    public JsonObjectInjector userFilter() {
        return userFilter(initSize);
    }


    /**
     * 是否开启过滤器
     *
     * @param initSize 注入前JsonObject.size()值
     * @return this
     */
    public JsonObjectInjector userFilter(int initSize) {
        jsonObject.addProperty("userFilter", jsonObject.size() > initSize ? 1 : 0);
        return this;
    }

    public JsonObjectInjector timeInterval(int timeInterval) {
        jsonObject.addProperty("timeInterval", timeInterval);
        return this;
    }

    public JsonObjectInjector remove(String ... removeKeys) {
        for (String removeKey : removeKeys) {
            jsonObject.remove(removeKey);
        }
        return this;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public JsonObject get() {
        return jsonObject;
    }

    public JsonObject json() {
        return jsonObject;
    }

    public OperationContext getContext() {
        return context;
    }
}