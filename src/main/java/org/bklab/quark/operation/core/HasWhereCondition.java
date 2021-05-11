package org.bklab.quark.operation.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface HasWhereCondition {

    default String createWhereCondition() {
        List<String> whereConditions = createWhereConditions(new ArrayList<>()).stream().filter(Objects::nonNull).collect(Collectors.toList());
        return whereConditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", whereConditions);
    }

    List<String> createWhereConditions(List<String> conditions);
}
