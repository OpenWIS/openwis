package org.openwis.metadataportal.kernel.user;

import org.apache.commons.codec.binary.Base32;

import java.nio.charset.Charset;
import java.util.Random;

/**
 * Created by cosmin on 23/07/19.
 */
public class TwoFactorAuthenticationUtils {

    private final static char[] HEX = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private final static int KEY_LENGTH = 12;
    /**
     * Encode {@param key} to Base16
     * @param key secret key
     * @return secret key encoded to Base16
     */
    public static String encodeBase16(String key) {
        byte[] byteArray = key.getBytes();
        StringBuffer hexBuffer = new StringBuffer(byteArray.length * 2);
        for (int i = 0; i < byteArray.length; i++)
            for (int j = 1; j >= 0; j--)
                hexBuffer.append(HEX[(byteArray[i] >> (j * 4)) & 0xF]);
        return hexBuffer.toString();
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
     * @param contactEmail user email
     * @param secretKey the secret key encoded in Base32
     * @return uri
     */
    public static String getTOPTKeyUri(String contactEmail, String secretKey) {
        StringBuilder uriBuilder = new StringBuilder("otpauth://totp/OpenWIS:");
        uriBuilder.append(contactEmail);
        uriBuilder.append("?secret=").append(secretKey);
        uriBuilder.append("&issuer=OpenWIS&period=30");
        return uriBuilder.toString();
    }
}
