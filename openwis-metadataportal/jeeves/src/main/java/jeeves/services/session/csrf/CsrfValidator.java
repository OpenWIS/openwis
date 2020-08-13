package jeeves.services.session.csrf;

import jeeves.server.UserSession;

public interface CsrfValidator extends Csrf {

    public boolean validateCsrfToken(UserSession session,String token);
}
