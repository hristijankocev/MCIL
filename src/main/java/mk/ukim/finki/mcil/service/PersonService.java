package mk.ukim.finki.mcil.service;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> save(String firstName, String lastName, byte[] profilePicture, List<Workplace> worksAtLinks,
                          List<WebPage> crawledLinks, List<WebPage> validLinks, String facebookAbout, String linkedInData);

    List<Person> listAll();

    void deleteById(Long id);

    Optional<Person> findById(Long id);
}
