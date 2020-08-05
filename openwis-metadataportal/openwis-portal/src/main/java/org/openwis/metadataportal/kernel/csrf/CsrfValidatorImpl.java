package org.openwis.metadataportal.kernel.csrf;

import jeeves.server.UserSession;
import jeeves.services.session.csrf.CsrfValidator;

import java.util.Optional;

public class CsrfValidatorImpl implements CsrfValidator {

    public CsrfValidatorImpl() {}

    @Override
    public boolean validateCsrfToken(UserSession session, String token) {
        Optional<String> sessionToken = Optional.ofNullable((String) session.getsHttpSession().getAttribute("csrf-token"));
        return sessionToken.map(s -> s.equals(token)).orElse(false);
    }
}
