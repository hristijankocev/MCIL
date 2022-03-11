package mk.ukim.finki.mcil.service.impl;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;
import mk.ukim.finki.mcil.model.enums.LinkStatus;
import mk.ukim.finki.mcil.model.exception.PersonNotFoundException;
import mk.ukim.finki.mcil.model.exception.WorkplaceNotFoundException;
import mk.ukim.finki.mcil.persistence.jpa.PersonRepository;
import mk.ukim.finki.mcil.service.PersonService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

}
