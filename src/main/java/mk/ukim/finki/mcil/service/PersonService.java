package mk.ukim.finki.mcil.service;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person save(String firstName, String lastName, byte[] profilePicture, List<Workplace> worksAtLinks,
                List<WebPage> crawledLinks, List<WebPage> validLinks, String facebookAbout, String linkedInData);

    List<Person> listAll();

    void deleteById(Long id);

    Optional<Person> findById(Long id);

    Person save(Person person);

    Optional<Workplace> getWorkplace(Person person, String wid);

    boolean validateImage(MultipartFile file) throws IOException;
}
