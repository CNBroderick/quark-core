package org.bklab.quark.util.security;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SM4UtilTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void getUserHome() {
        System.getProperties().forEach((a, b) -> System.out.println(a + " = " + b));
    }

    @Test
    void encode() {
        logger.info("encode = " + SM4Util.encode("broderick"));
    }

    @Test
    void decode() {
        logger.info("decode = " + SM4Util.encode("1c76ac527b679acd42ce5e5a519909aa"));
    }
}
