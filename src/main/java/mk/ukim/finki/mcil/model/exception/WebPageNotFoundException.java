package mk.ukim.finki.mcil.model.exception;

public class WebPageNotFoundException extends RuntimeException {
    public WebPageNotFoundException(Long id) {
        super(String.format("Webpage with id \"%d\" not found.", id));
    }
}
