package org.bklab.quark.operation.core;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface HasQueryEntities<T> {

    default T queryFirstEntity(Connection connection) throws Exception {
        return queryEntities(connection).stream().findFirst().orElse(null);
    }

    default <K> Map<K, T> queryEntityMap(Connection connection, Function<T, K> keyFunction) throws Exception {
        return queryEntities(connection).stream().collect(
                Collectors.toMap(keyFunction, Function.identity(), (a, b) -> b, LinkedHashMap::new)
        );
    }

    List<T> queryEntities(Connection connection) throws Exception;

}
