package mk.ukim.finki.mcil.model.exception;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(Long id) {
        super(String.format("Person with id \"%d\" not found.", id));
    }
}
