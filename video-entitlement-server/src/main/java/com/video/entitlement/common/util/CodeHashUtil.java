package com.video.entitlement.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class CodeHashUtil {

    private CodeHashUtil() {}

    public static String hashWithHmac(String code, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            mac.init(spec);
            byte[] hash = mac.doFinal(code.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC-SHA256 hash failed", e);
        }
    }

    public static String maskCode(String code) {
        if (code == null || code.length() <= 4) return "****";
        return code.substring(0, 2) + "****" + code.substring(code.length() - 2);
    }

    public static String generateCode(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789abcdefghjkmnpqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
