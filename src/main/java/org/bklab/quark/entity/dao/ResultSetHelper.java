package org.bklab.quark.entity.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ResultSetHelper implements Supplier<ResultSet> {

    private final ResultSet resultSet;
    private final Logger logger;

    public ResultSetHelper(ResultSet resultSet) {
        this.resultSet = resultSet;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public ResultSetHelper(ResultSet resultSet, Logger logger) {
        this.resultSet = resultSet;
        this.logger = logger;
    }

    public <T> List<T> asList(IEntityRowMapper<T> rowMapper) throws Exception {
        List<T> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                list.add(rowMapper.mapRow(resultSet));
            }
        } catch (Exception e) {
            logger.error("解析Entity Row Mapper错误。", e);
            throw e;
        } finally {
            resultSet.close();
        }
        return list;
    }

    public Integer countReturnRowSize() throws Exception {
        return asList(a -> a).size();
    }

    public List<String> asStringList() throws Exception {
        return asList(s -> s.getString(1));
    }

    public List<Integer> asIntList() throws Exception {
        return asList(s -> s.getInt(1));
    }

    public <T> T asObject(IEntityRowMapper<T> rowMapper) throws Exception {
        try {
            if (resultSet.next()) return rowMapper.mapRow(resultSet);
        } catch (Exception e) {
            logger.error("解析Entity Row Mapper错误。", e);
            throw e;
        } finally {
            resultSet.close();
        }
        return null;
    }

    public Long asLong() throws Exception {
        return asObject(r -> r.getLong(1));
    }

    public Integer asInt() throws Exception {
        return asObject(r -> r.getInt(1));
    }

    public Double asDouble() throws Exception {
        return asObject(r -> r.getDouble(1));
    }


    public Float asFloat() throws Exception {
        return asObject(r -> r.getFloat(1));
    }

    public Time asTime() throws Exception {
        return asObject(r -> r.getTime(1));
    }

    public LocalTime asLocalTime() throws Exception {
        return Optional.ofNullable(asObject(r -> r.getTime(1))).map(Time::toLocalTime).orElse(null);
    }

    public Date asDate() throws Exception {
        return asObject(r -> r.getDate(1));
    }

    public LocalDate asLocalDate() throws Exception {
        return Optional.ofNullable(asObject(r -> r.getDate(1))).map(Date::toLocalDate).orElse(null);
    }

    public LocalDateTime asLocalDateTime() throws Exception {
        return LocalDateTime.of(asLocalDate(), asLocalTime());
    }

    public Map<String, String> asMap() throws Exception {
        return asMap(r -> r.getString(1), b -> b.getString(2));
    }

    public <V> Map<String, V> asStringObjectMap(IEntityRowMapper<V> toValueFunction) throws Exception {
        return asMap(r -> r.getString(1), toValueFunction);
    }

    public <K, V> Map<K, V> asMap(IEntityRowMapper<K> toKeyFunction, IEntityRowMapper<V> toValueFunction) throws Exception {
        return asMap(toKeyFunction, toValueFunction, (a, b) -> b);
    }

    public <K, V> Map<K, V> asMap(IEntityRowMapper<K> toKeyFunction, IEntityRowMapper<V> toValueFunction,
                                  BiFunction<V, V, V> mergeValueFunction) throws Exception {
        Map<K, V> map = new LinkedHashMap<>();
        try {
            while (resultSet.next()) {
                K k = toKeyFunction.mapRow(resultSet);
                V v = toValueFunction.mapRow(resultSet);
                if (map.containsKey(k) && map.get(k) != null) {
                    map.put(k, mergeValueFunction.apply(map.get(k), v));
                } else {
                    map.put(k, v);
                }
            }
        } catch (Exception e) {
            logger.error("解析Entity Row Mapper错误。", e);
            throw e;
        } finally {
            resultSet.close();

        }
        return map;
    }

    public <K, C extends Collection<V>, V> Map<K, C> asMap(IEntityRowMapper<K> toKeyFunction,
                                                                IEntityRowMapper<V> toValueFunction,
                                                                Supplier<C> valueCollectionSupplier,
                                                                boolean skipNull) throws Exception {

        Map<K, C> map = new LinkedHashMap<>();
        try {
            while (resultSet.next()) {
                K k = toKeyFunction.mapRow(resultSet);
                V v = toValueFunction.mapRow(resultSet);
                if (v != null || !skipNull) {
                    C vList = map.getOrDefault(k, valueCollectionSupplier.get());
                    vList.add(v);
                    map.put(k, vList);
                }
            }
        } catch (Exception e) {
            logger.error("解析Entity Row Mapper错误。", e);
            throw e;
        } finally {
            resultSet.close();
        }
        return map;
    }

    public <K, V> Map<K, List<V>> asListMap(IEntityRowMapper<K> toKeyFunction, IEntityRowMapper<V> toValueFunction, boolean skipNull) throws Exception {
        return asMap(toKeyFunction,toValueFunction,ArrayList::new, skipNull);
    }

    public <K, V> Map<K, Set<V>> asSetMap(IEntityRowMapper<K> toKeyFunction, IEntityRowMapper<V> toValueFunction, boolean skipNull) throws Exception {
        return asMap(toKeyFunction,toValueFunction,LinkedHashSet::new, skipNull);
    }

    public <T> Set<T> asSet(IEntityRowMapper<T> entityRowMapper) throws Exception {
        return new LinkedHashSet<>(asList(entityRowMapper));
    }

    public <T> void setEntityGeneratedKeys(Collection<T> entities, BiConsumer<T, Integer> idConsumer) throws Exception {
        Integer id = asInt();
        if (id != null && entities != null) {
            for (T entity : entities) {
                idConsumer.accept(entity, id++);
            }
        }
    }

    /**
     * 设置自增id的值，仅适用 包含 int id; 的类
     *
     * @param entity 待负id值的对象
     * @param <T>    仅适用 包含 int id; 的类
     * @throws Exception <ul>
     *                   <li>未找到字段id </li>
     *                   <li>未设置 connection.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);</li>
     *                   </ul>
     */
    public <T> void setEntityGeneratedKeys(T entity) throws Exception {
        Field id = entity.getClass().getDeclaredField("id");
        id.setAccessible(true);
        id.setInt(entity, asInt());
    }

    public <T> void setEntityGeneratedKeys(T entity, BiConsumer<T, Integer> idConsumer) throws Exception {
        idConsumer.accept(entity, asInt());
    }

    @Override
    public ResultSet get() {
        return resultSet;
    }
}
