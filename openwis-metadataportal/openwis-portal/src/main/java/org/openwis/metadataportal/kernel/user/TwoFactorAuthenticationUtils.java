package org.openwis.metadataportal.kernel.user;

import org.apache.commons.codec.binary.Base32;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by cosmin on 23/07/19.
 */
public class TwoFactorAuthenticationUtils {

    private final static int KEY_LENGTH = 12;

    /**
     * Encode {@param key} to Base16
     * @param key secret key
     * @return secret key encoded to Base16
     */
    public static String encodeBase16(String key) {
        char[] encoded = Base16.encode(key.getBytes(), false);
        return new String(encoded);
    }

    public static String decodeBase16(String key) {
        byte[] bytes = Base16.decode(key.toCharArray());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Encode to base32
     * @param key secret key
     * @return secret key encoded to Base32
     */
    public static String encodeBase32(String key) {
        Base32 base32 = new Base32();
        return base32.encodeAsString(key.getBytes());
    }

    /**
     * Generate a secret key
     * @return secret key
     */
    public static String generateKey() {
        byte[] array = new byte[KEY_LENGTH];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    /**
     * Return a uri used in Google Authenticator
     * e.g. otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
     * @param userEmail user email
     * @param secretKey the secret key encoded in Base32
     * @return uri
     */
    public static String getTOPTKeyUri(String userEmail, String secretKey) {
        StringBuilder uriBuilder = new StringBuilder("otpauth://totp/OpenWIS:");
        uriBuilder.append(userEmail);
        uriBuilder.append("?secret=").append(secretKey);
        uriBuilder.append("&issuer=OpenWIS&period=30");
        return uriBuilder.toString();
    }
}
