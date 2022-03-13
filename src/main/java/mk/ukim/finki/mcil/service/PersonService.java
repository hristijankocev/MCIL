package mk.ukim.finki.mcil.service;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;
import mk.ukim.finki.mcil.model.dto.EditPersonDTO;
import mk.ukim.finki.mcil.model.dto.PersonDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonService {
    Person addPerson(String firstName, String lastName, byte[] profilePicture, List<Workplace> worksAtLinks, List<WebPage> webPages, String facebookAbout, String linkedInData);

    List<Person> listAll();

    void deleteById(Long id);

    Person findById(Long id);

    Person save(Person person);

    Person addPerson(PersonDTO addPersonDTO);

    Person editPerson(PersonDTO personDTO, Person oldPerson);

    Optional<Workplace> getWorkplace(Person person, String wid);

    Map<String, String> getGoogleQueryResults(String query);

    Person changePfp(String pid, MultipartFile file) throws IOException;

    EditPersonDTO convertToDTO(Long personId);

    Person removeWorkplace(Long personId, String workplaceId);
}
