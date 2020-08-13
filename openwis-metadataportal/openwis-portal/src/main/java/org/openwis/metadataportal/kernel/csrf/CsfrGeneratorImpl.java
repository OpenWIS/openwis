package org.openwis.metadataportal.kernel.csrf;

import jeeves.services.session.csrf.CsrfGenerator;

import java.security.SecureRandom;
import java.util.Base64;

public class CsfrGeneratorImpl implements CsrfGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public CsfrGeneratorImpl() {}

    public String generateCsrfToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
