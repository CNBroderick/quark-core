package org.bklab.quark.util.map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParameterMapConvertor implements Function<Map<String, Supplier<Object>>, Map<String, Object>> {
    @Override
    public Map<String, Object> apply(Map<String, Supplier<Object>> parameterMap) {
        Map<String, Object> map = new LinkedHashMap<>();
        parameterMap.forEach((k, v) -> Optional.ofNullable(v).map(Supplier::get).ifPresent(o -> map.put(k, o)));
        return map;
    }
}
