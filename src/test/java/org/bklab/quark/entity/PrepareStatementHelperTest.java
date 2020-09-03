package org.bklab.quark.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrepareStatementHelperTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void executeUpdate() {
        System.out.println(a());
    }


    private int a() {
        try {
            return 1;
        } catch (Exception e) {
            return -1;
        } finally {
            System.out.println("executed");
        }
    }

    @Test
    void testExecuteUpdate() {
    }

    @Test
    void executeQuery() {
    }

    @Test
    void executeQueryObject() {
    }

    @Test
    void close() {
    }
}