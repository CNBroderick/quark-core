package org.bklab.quark.entity;

import com.google.gson.JsonArray;
import org.bklab.quark.entity.converter.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

public class EntityConverterManager implements Function<Class<?>, StringToEntityConverter<?>> {

    private static final EntityConverterManager instance = new EntityConverterManager();

    private final StringToDoubleConverter stringToDoubleConverter = new StringToDoubleConverter();
    private final StringToFloatConverter stringToFloatConverter = new StringToFloatConverter();
    private final StringToIntegerConverter stringToIntegerConverter = new StringToIntegerConverter();
    private final StringToLocalDateConverter stringToLocalDateConverter = new StringToLocalDateConverter();
    private final StringToLocalDateTimeConverter stringToLocalDateTimeConverter = new StringToLocalDateTimeConverter();
    private final StringToLocalTimeConverter stringToLocalTimeConverter = new StringToLocalTimeConverter();
    private final StringToLongConverter stringToLongConverter = new StringToLongConverter();
    private final StringToJsonConverter stringToJsonConverter = new StringToJsonConverter();
    private final StringToJsonArrayConverter stringToJsonArrayConverter = new StringToJsonArrayConverter();
    private final StringToEntityConverter<String> stringToStringConverter = s -> s;

    public static EntityConverterManager getInstance() {
        return instance;
    }


    @Override
    public StringToEntityConverter<?> apply(Class<?> target) {

        if (String.class.equals(target)) return stringToStringConverter;
        if (Double.class.equals(target)) return stringToDoubleConverter;
        if (Float.class.equals(target)) return stringToFloatConverter;
        if (Integer.class.equals(target)) return stringToIntegerConverter;
        if (LocalDate.class.equals(target)) return stringToLocalDateConverter;
        if (LocalDateTime.class.equals(target)) return stringToLocalDateTimeConverter;
        if (LocalTime.class.equals(target)) return stringToLocalTimeConverter;
        if (Long.class.equals(target)) return stringToLongConverter;
        if (StringToJsonConverter.class.equals(target)) return stringToJsonConverter;
        if (JsonArray.class.equals(target)) return stringToJsonArrayConverter;

        return null;
    }
}
