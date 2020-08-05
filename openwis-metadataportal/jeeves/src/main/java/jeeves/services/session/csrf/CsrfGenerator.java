package jeeves.services.session.csrf;

public interface CsrfGenerator extends Csrf {
    public String generateCsrfToken();
}
