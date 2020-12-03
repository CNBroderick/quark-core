package org.bklab.quark.util.security;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;

public class ExamineUtil {
    public static final String Algorithm_SHA_1 = "SHA-1";
    public static final String Algorithm_SHA1 = "SHA1";
    public static final String Algorithm_SHA_384 = "SHA-384";
    public static final String Algorithm_OID_1_3_14_3_2_26 = "OID.1.3.14.3.2.26";
    public static final String Algorithm_SHA = "SHA";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_4 = "OID.2.16.840.1.101.3.4.2.4";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_3 = "OID.2.16.840.1.101.3.4.2.3";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_2 = "OID.2.16.840.1.101.3.4.2.2";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_1 = "OID.2.16.840.1.101.3.4.2.1";
    public static final String Algorithm_1_3_14_3_2_26 = "1.3.14.3.2.26";
    public static final String Algorithm_SHA3_384 = "SHA3-384";
    public static final String Algorithm_SHA_224 = "SHA-224";
    public static final String Algorithm_SHA_512_224 = "SHA-512/224";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_9 = "OID.2.16.840.1.101.3.4.2.9";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_8 = "OID.2.16.840.1.101.3.4.2.8";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_7 = "OID.2.16.840.1.101.3.4.2.7";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_6 = "OID.2.16.840.1.101.3.4.2.6";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_5 = "OID.2.16.840.1.101.3.4.2.5";
    public static final String Algorithm_SHA3_224 = "SHA3-224";
    public static final String Algorithm_SHA3_512 = "SHA3-512";
    public static final String Algorithm_2_16_840_1_101_3_4_2_9 = "2.16.840.1.101.3.4.2.9";
    public static final String Algorithm_2_16_840_1_101_3_4_2_6 = "2.16.840.1.101.3.4.2.6";
    public static final String Algorithm_2_16_840_1_101_3_4_2_5 = "2.16.840.1.101.3.4.2.5";
    public static final String Algorithm_2_16_840_1_101_3_4_2_8 = "2.16.840.1.101.3.4.2.8";
    public static final String Algorithm_2_16_840_1_101_3_4_2_7 = "2.16.840.1.101.3.4.2.7";
    public static final String Algorithm_2_16_840_1_101_3_4_2_2 = "2.16.840.1.101.3.4.2.2";
    public static final String Algorithm_2_16_840_1_101_3_4_2_1 = "2.16.840.1.101.3.4.2.1";
    public static final String Algorithm_2_16_840_1_101_3_4_2_4 = "2.16.840.1.101.3.4.2.4";
    public static final String Algorithm_2_16_840_1_101_3_4_2_3 = "2.16.840.1.101.3.4.2.3";
    public static final String Algorithm_2_16_840_1_101_3_4_2_10 = "2.16.840.1.101.3.4.2.10";
    public static final String Algorithm_OID_2_16_840_1_101_3_4_2_10 = "OID.2.16.840.1.101.3.4.2.10";
    public static final String Algorithm_SHA_512_256 = "SHA-512/256";
    public static final String Algorithm_SHA_256 = "SHA-256";
    public static final String Algorithm_MD2 = "MD2";
    public static final String Algorithm_SHA3_256 = "SHA3-256";
    public static final String Algorithm_SHA_512 = "SHA-512";
    public static final String Algorithm_MD5 = "MD5";

    private final MessageDigest messageDigest;

    public ExamineUtil(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }

    public static ExamineUtil create(String algorithm) {
        try {
            return new ExamineUtil(MessageDigest.getInstance(algorithm));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ExamineUtil sha_1() {
        return create(Algorithm_SHA_1);
    }

    public static ExamineUtil sha1() {
        return create(Algorithm_SHA1);
    }

    public static ExamineUtil sha_384() {
        return create(Algorithm_SHA_384);
    }

    public static ExamineUtil sha() {
        return create(Algorithm_SHA);
    }

    public static ExamineUtil sha3_384() {
        return create(Algorithm_SHA3_384);
    }

    public static ExamineUtil sha_224() {
        return create(Algorithm_SHA_224);
    }

    public static ExamineUtil sha_512_224() {
        return create(Algorithm_SHA_512_224);
    }

    public static ExamineUtil sha3_224() {
        return create(Algorithm_SHA3_224);
    }

    public static ExamineUtil sha3_512() {
        return create(Algorithm_SHA3_512);
    }

    public static ExamineUtil sha_512_256() {
        return create(Algorithm_SHA_512_256);
    }

    public static ExamineUtil sha_256() {
        return create(Algorithm_SHA_256);
    }

    public static ExamineUtil md2() {
        return create(Algorithm_MD2);
    }

    public static ExamineUtil sha3_256() {
        return create(Algorithm_SHA3_256);
    }

    public static ExamineUtil sha_512() {
        return create(Algorithm_SHA_512);
    }

    public static ExamineUtil md5() {
        return create(Algorithm_MD5);
    }

    public static Set<String> getAvailableAlgorithm() {
        final String serviceType = "MessageDigest";
        Set<String> result = new HashSet<>();

        for (Provider provider : Security.getProviders()) {
            for (Object o : provider.keySet()) {
                String key = ((String) o).split(" ")[0];
                if (key.startsWith(serviceType + ".")) {
                    result.add(key.substring(serviceType.length() + 1));
                } else if (key.startsWith("Alg.Alias." + serviceType + ".")) {
                    result.add(key.substring(serviceType.length() + 11));
                }
            }
        }

        return result;
    }

    public ExamineUtil update(byte[] input, int offset, int len) {
        messageDigest.update(input, offset, len);
        return this;
    }

    public ExamineUtil update(String string) {
        messageDigest.update(string.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public ExamineUtil update(byte[] input) {
        messageDigest.update(input);
        return this;
    }

    public ExamineUtil update(ByteBuffer bytes) {
        messageDigest.update(bytes);
        return this;
    }

    public ExamineUtil update(Path path) throws IOException {
        messageDigest.update(Files.readAllBytes(path));
        return this;
    }

    public String calc(byte[] input, int offset, int len) {
        return update(input, offset, len).get();
    }

    public String calc(String string) {
        return update(string.getBytes(StandardCharsets.UTF_8)).get();
    }

    public String calc(byte[] input) {
        return update(input).get();
    }

    public String calc(ByteBuffer bytes) {
        return update(bytes).get();
    }

    public String calc(Path path) throws IOException {
        return update(Files.readAllBytes(path)).get();
    }

    public String get() {
        return Hex.encodeHexString(messageDigest.digest());
    }
}
