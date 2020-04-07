package org.openwis.metadataportal.kernel.user;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by cosmin on 23/07/19.
 */
public class TwoFactorAuthenticationUtils {

    /**
     * Encoding in base16 is needed by OpenAM
     * Encode {@param key} to Base16
     * @param key secret key
     * @return secret key encoded to Base16
     */
    public static String encodeBase16(String key) {
        return Hex.encodeHexString(key.getBytes());
    }

    public static String decodeBase16(String key) {
        try {
            return new String(Hex.decodeHex(key.toCharArray()));
        } catch (DecoderException e) {
            return "";
        }
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

    public static String decodeBase32(String key) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(key);
        return new String(bytes);
    }

    /**
     * Generate a secret key encoded in base16
     * @return secret key
     */
    public static String generateKey() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return Hex.encodeHexString(generatedString.getBytes());
    }

    /**
     * Return a uri used in Google Authenticator
     * e.g. otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
     * @param userEmail user email
     * @param secretKey the secret key encoded in Base32
     * @return uri
     */
    public static String getGoogleAuthenticatorBarCode(String userEmail, String secretKey) throws IllegalArgumentException {
        try {
            String issuer = "OpenWIS";
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + userEmail, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
