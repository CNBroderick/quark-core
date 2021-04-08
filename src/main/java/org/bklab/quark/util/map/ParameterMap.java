package org.bklab.quark.util.map;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ParameterMap extends LinkedHashMap<String, Supplier<Object>> {

    public ParameterMap() {
    }

    public ParameterMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ParameterMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ParameterMap(Map<? extends String, ? extends Supplier<Object>> m) {
        super(m == null ? Collections.emptyMap() : m);
    }

    public ParameterMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public static ParameterMap create(LinkedHashMap<String, Supplier<Object>> map) {
        return map instanceof ParameterMap ? (ParameterMap) map : new ParameterMap(map);
    }

    public LocalDateTime getMinDatetime() {
        return Optional.ofNullable(get("minDatetime")).map(Supplier::get)
                .filter(LocalDateTime.class::isInstance).map(LocalDateTime.class::cast).orElse(null);
    }

    public LocalDateTime getMaxDatetime() {
        return Optional.ofNullable(get("maxDatetime")).map(Supplier::get)
                .filter(LocalDateTime.class::isInstance).map(LocalDateTime.class::cast).orElse(null);
    }

    public <T> T getValue(String key) {
        //noinspection unchecked
        return (T) Optional.ofNullable(get(key)).map(Supplier::get).orElse(null);
    }

    public Optional<Object> getOptionalValue(String key) {
        return Optional.ofNullable(get(key)).map(Supplier::get);
    }

    public Map<String, Object> createParameterMap() {
        return new ParameterMapConvertor().apply(this);
    }
}
