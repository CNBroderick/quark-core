package org.bklab.quark.operation.http;

import dataq.core.operation.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenerateOperationResult extends OperationResult {

    @Override
    public <T> List<T> asList() {
        return Optional.ofNullable(super.<T>asList()).orElse(new ArrayList<>());
    }

}
