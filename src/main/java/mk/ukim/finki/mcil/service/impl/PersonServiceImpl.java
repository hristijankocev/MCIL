package mk.ukim.finki.mcil.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;
import mk.ukim.finki.mcil.model.dto.EditPersonDTO;
import mk.ukim.finki.mcil.model.dto.PersonDTO;
import mk.ukim.finki.mcil.model.enums.LinkStatus;
import mk.ukim.finki.mcil.model.exception.PersonNotFoundException;
import mk.ukim.finki.mcil.model.exception.WorkplaceNotFoundException;
import mk.ukim.finki.mcil.persistence.jpa.PersonRepository;
import mk.ukim.finki.mcil.service.PersonService;
import mk.ukim.finki.mcil.service.WorkplaceService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final WorkplaceService workplaceService;

    @Override
    public Person addPerson(String firstName, String lastName, byte[] profilePicture,
                            List<Workplace> worksAtLinks, List<WebPage> webPages, String facebookAbout, String linkedInData) {
        return this.personRepository
                .save(new Person(firstName, lastName, profilePicture, worksAtLinks, webPages, facebookAbout, linkedInData));
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
    public Person findById(Long id) {
        return this.personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @Override
    public Person save(Person person) {
        return this.personRepository.save(person);
    }

    @Override
    public Person addPerson(PersonDTO changes) {
        Person person = new Person();

        person.setFirstName(changes.getFirstName());
        person.setLastName(changes.getLastName());

        List<Workplace> worksAtList = new ArrayList<>();
        List<WebPage> webPageList = new ArrayList<>();

        return setListsAndSavePerson(changes, person, worksAtList, webPageList);
    }

    @Override
    public Person editPerson(PersonDTO personDTO, Person person) {
        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        person.setLinkedInData(personDTO.getLinkedinData());
        person.setFacebookAbout(personDTO.getFacebookAbout());

        List<Workplace> worksAtList = person.getWorksAtLinks();
        List<WebPage> webPageList = person.getWebPages();

        return setListsAndSavePerson(personDTO, person, worksAtList, webPageList);
    }

    private Person setListsAndSavePerson(PersonDTO personDTO, Person person, List<Workplace> worksAtList, List<WebPage> webPageList) {
        if (!personDTO.getWorksAt().isEmpty()) {
            for (String link : personDTO.getWorksAt().trim().split(";")) {
                link = link.trim();
                if (link.length() > 0) {
                    String finalLink = link;
                    if (worksAtList.stream().noneMatch(w -> w.getName().equals(finalLink)))
                        worksAtList.add(this.workplaceService.save(link));
                }
            }
            person.setWorksAtLinks(worksAtList);
        }

        if (!personDTO.getValidLinks().isEmpty()) {
            for (String link : personDTO.getValidLinks().trim().split(";")) {
                link = link.trim();
                if (link.length() > 0) {
                    String finalLink = link;
                    if (webPageList.stream().noneMatch(l -> l.getLink().equals(finalLink)))
                        webPageList.add(new WebPage(finalLink, LinkStatus.VALID, person, ""));
                }
            }
        }


        if (!personDTO.getCrawledLinks().isEmpty()) {
            for (String link : personDTO.getCrawledLinks().trim().split(";")) {
                link = link.trim();
                if (link.length() > 0) {
                    String finalLink = link;
                    if (webPageList.stream().noneMatch(l -> l.getLink().equals(finalLink)))
                        webPageList.add(new WebPage(link, LinkStatus.CRAWLED, person, ""));
                }
            }
        }
        person.setWebPages(webPageList);

        this.personRepository.save(person);

        return person;
    }

    @Override
    public Optional<Workplace> getWorkplace(Person person, String wid) {
        return Optional.of(person.getWorksAtLinks().stream()
                .filter(w -> w.getName().equals(wid))
                .findAny()
                .orElseThrow(() -> new WorkplaceNotFoundException(wid)));
    }

    /**
     * @param query: the thing we want to search on google
     * @return returns a Map in the form of Title(google result title),Link(the link for the google result)
     */
    @Override
    public Map<String, String> getGoogleQueryResults(String query) {
        Map<String, String> dict = new HashMap<>();
        final String connUrl = "https://www.google.com/search?q=" + query.replaceAll(" ", "+");

        try {
            // User-Agent matters, google outputs different results for different User-Agents
            final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.87 Safari/537.36";
            // Fetch the page
            final Document doc = Jsoup.connect(connUrl).userAgent(USER_AGENT).get();
            // Select and traverse the elements that contain the links and extract the info
            for (Element result : doc.select("div.yuRUbf > a")) {
                final String title = result.text();
                final String url = result.attr("href");
                // Store the result in a dictionary
                dict.put(title, url);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return dict;
    }

    @Override
    public Person changePfp(String pid, MultipartFile file) throws IOException {
        Person person = this.findById(Long.parseLong(pid));
        if (ImageIO.read(file.getInputStream()) != null) {
            person.setProfilePicture(file.getBytes());
            this.save(person);
        }
        return person;
    }

    @Override
    public EditPersonDTO convertToDTO(Long personId) {
        EditPersonDTO personDTO = new EditPersonDTO();
        Person person = this.findById(personId);

        personDTO.setId(personId);
        personDTO.setFirstName(person.getFirstName());
        personDTO.setLastName(person.getLastName());
        personDTO.setFacebookAbout(person.getFacebookAbout());
        personDTO.setLinkedInData(person.getLinkedInData());
        personDTO.setWorksAt(person.getWorksAtLinks());
        personDTO.setProfilePicture(person.generateBase64Image());

        personDTO.setCrawledLinks(person.getWebPages().stream()
                .filter(w -> w.getStatus().equals(LinkStatus.CRAWLED))
                .collect(Collectors.toList()));

        personDTO.setValidLinks(person.getWebPages().stream()
                .filter(w -> w.getStatus().equals(LinkStatus.VALID))
                .collect(Collectors.toList()));

        return personDTO;
    }

    @Override
    public Person removeWorkplace(Long personId, String workplaceId) {
        Person person = this.findById(personId);
        Workplace workplace = this.workplaceService.findById(workplaceId);
        person.getWorksAtLinks().remove(workplace);
        this.save(person);
        return person;
    }


}
