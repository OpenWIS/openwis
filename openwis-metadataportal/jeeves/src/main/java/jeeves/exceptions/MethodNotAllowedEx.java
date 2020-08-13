package jeeves.exceptions;

public class MethodNotAllowedEx  extends NotAllowedEx{

    public MethodNotAllowedEx(String message) {
        super("Method not allowed", message);

        id   = "method-not-allowed";
        code = 405;
    }
}
