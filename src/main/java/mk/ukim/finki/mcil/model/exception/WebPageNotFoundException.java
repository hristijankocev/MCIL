package mk.ukim.finki.mcil.model.exception;

public class WebPageNotFoundException extends RuntimeException {
    public WebPageNotFoundException(String id) {
        super(String.format("Webpage with id \"%s\" not found.", id));
    }
}
