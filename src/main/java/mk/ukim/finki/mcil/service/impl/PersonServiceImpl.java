package mk.ukim.finki.mcil.service.impl;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;
import mk.ukim.finki.mcil.model.enums.LinkStatus;
import mk.ukim.finki.mcil.model.exception.PersonNotFoundException;
import mk.ukim.finki.mcil.model.exception.WorkplaceNotFoundException;
import mk.ukim.finki.mcil.persistence.jpa.PersonRepository;
import mk.ukim.finki.mcil.service.PersonService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person save(String firstName, String lastName, byte[] profilePicture,
                       List<Workplace> worksAtLinks, List<WebPage> crawledLinks,
                       List<WebPage> validLinks, String facebookAbout, String linkedInData) {
        return this.personRepository
                .save(new Person(firstName, lastName, profilePicture, worksAtLinks, crawledLinks, validLinks,
                        facebookAbout, linkedInData));
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

    @Override
    public Person save(Person person) {
        return this.personRepository.save(person);
    }

    @Override
    public Optional<Workplace> getWorkplace(Person person, String wid) {
        return Optional.of(person.getWorksAtLinks().stream()
                .filter(w -> w.getName().equals(wid))
                .findAny()
                .orElseThrow(() -> new WorkplaceNotFoundException(wid)));
    }

    @Override
    public boolean validateImage(MultipartFile file) throws IOException {
        return ImageIO.read(file.getInputStream()) != null;
    }

}
