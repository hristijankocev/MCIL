package mk.ukim.finki.mcil.service.impl;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;
import mk.ukim.finki.mcil.model.exception.PersonNotFoundException;
import mk.ukim.finki.mcil.persistence.jpa.PersonRepository;
import mk.ukim.finki.mcil.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Optional<Person> save(String firstName, String lastName, byte[] profilePicture,
                                 List<Workplace> worksAtLinks, List<WebPage> crawledLinks,
                                 List<WebPage> validLinks, String facebookAbout, String linkedInData) {
        return Optional.of(this.personRepository
                .save(new Person(firstName, lastName, profilePicture, worksAtLinks, crawledLinks, validLinks,
                        facebookAbout, linkedInData)));
    }

    @Override
    public List<Person> listAll() {
        return this.personRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        Person person = this.personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        this.personRepository.deleteById(person.getId());
    }

    @Override
    public Optional<Person> findById(Long id) {
        Person person = this.personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        return Optional.of(person);
    }
}
