package jeeves.exceptions;

public class BadRequestEx extends JeevesClientEx{

    public BadRequestEx(String name) {
        super("Bad request",name);

        id = "bad-request";
        code = 400;
    }
}
