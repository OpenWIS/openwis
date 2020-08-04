package jeeves.exceptions;

public class BadRequestEx extends JeevesClientEx{

    public BadRequestEx(String message, Object object) {
        super(message,object);

        id = "bad-request";
        code = 400;
    }
}
