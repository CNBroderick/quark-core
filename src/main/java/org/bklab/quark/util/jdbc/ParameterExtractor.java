package org.bklab.quark.util.jdbc;

import dataq.core.operation.OperationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParameterExtractor {

    public static <T, C extends Collection<T>> C process(OperationContext context, C to, Class<T> listClass) {
        String name = classNameToHumpName(listClass);
        return process(context, to, listClass, name, name + 's');
    }

    public static <T, C extends Collection<T>> C process(OperationContext context, C to, Class<T> listClass, Boolean allowEmpty) {
        String name = classNameToHumpName(listClass);
        return process(context, to, listClass, name, name + 's', allowEmpty);
    }

    public static <T, C extends Collection<T>> C process(
            OperationContext context, C to, Class<T> listClass, String entityName) {
        return process(context, to, listClass, entityName, entityName + 's', Boolean.FALSE);
    }

    public static <T, C extends Collection<T>> C process(
            OperationContext context, C to, Class<T> listClass, String entityName, Boolean allowEmpty) {
        return process(context, to, listClass, entityName, entityName + 's', allowEmpty);
    }

    public static <T, C extends Collection<T>> C process(
            OperationContext context, C to, Class<T> listClass, String entityName, String listName) {
        return process(context, to, listClass, entityName, listName, Boolean.FALSE);
    }

    public static <T, C extends Collection<T>> C process(
            OperationContext context, C to, Class<T> listClass, String entityName, String listName, Boolean allowEmpty) {
        Object entity = context.getParam(entityName);
        Object entities = context.getParam(listName);
        if (!allowEmpty && entity == null && entities == null) {
            throw new NullParameterException(listClass, entityName, listName);
        }
        if (listClass.isInstance(entity))
            to.add(listClass.cast(entity));
        if (entities instanceof Collection<?>) {
            for (Object item : (Collection<?>) entities) {
                if (listClass.isInstance(item))
                    to.add(listClass.cast(item));
            }
        }
        return to;
    }

    private static String classNameToHumpName(Class<?> listClass) {
        String name = listClass.getSimpleName();
        if (name.isEmpty())
            return name;
        if (name.contains("_")) {
            name = upperCaseAndSplit(name, "_");
        }
        if (name.contains("-")) {
            name = upperCaseAndSplit(name, "-");
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private static String upperCaseAndSplit(String source, String delimiter) {
        List<String> list = new ArrayList<>();
        for (String s : source.split(delimiter)) {
            char[] c = s.toCharArray();
            if (c.length > 0) {
                list.add(source.substring(0, 1).toUpperCase() + source.substring(1));
            }
        }
        return String.join("", list);
    }

    private static class NullParameterException extends RuntimeException {
        private NullParameterException(Class<?> listClass, String entityName, String listName) {
            super(String.format("\n  请设置参数名为[%s]且属于类[? extends %s]，\n  或设置参数名为[%s]且属于类Collection<? extends %s>",
                    entityName, listClass.getName(), listName, listClass.getName()));
        }
    }
}
