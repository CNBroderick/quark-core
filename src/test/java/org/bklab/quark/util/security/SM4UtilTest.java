package org.bklab.quark.util.security;

import org.junit.jupiter.api.Test;

class SM4UtilTest {

    @Test
    void encode() {
        System.out.println("encode = " + SM4Util.encode("broderick"));
    }

    @Test
    void decode() {
        System.out.println("decode = " + SM4Util.encode("1c76ac527b679acd42ce5e5a519909aa"));
    }
}
