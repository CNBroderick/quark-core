package org.bklab.quark.operation.internal;

import org.junit.jupiter.api.Test;

public class AbstractOperationBuilderTest {

    @Test
    public void test() {
        AbstractOperationBuilder.DefaultBuilder builder = AbstractOperationBuilder.getDefaultBuilder().addCallingClass();
        builder.getParameterMap().entrySet().forEach(System.out::println);
    }

}
