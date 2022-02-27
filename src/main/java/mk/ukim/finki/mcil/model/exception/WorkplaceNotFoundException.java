package mk.ukim.finki.mcil.model.exception;

public class WorkplaceNotFoundException extends RuntimeException {
    public WorkplaceNotFoundException(String id) {
        super(String.format("Workplace with id \"%s\" not found.", id));
    }
}
