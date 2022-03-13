package mk.ukim.finki.mcil.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.enums.LinkStatus;
import mk.ukim.finki.mcil.model.exception.WebPageNotFoundException;
import mk.ukim.finki.mcil.persistence.jpa.PersonRepository;
import mk.ukim.finki.mcil.persistence.jpa.WebPageRepository;
import mk.ukim.finki.mcil.service.WebPageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebPageServiceImpl implements WebPageService {

    private final WebPageRepository webPageRepository;
    private final PersonRepository personRepository;

    @Override
    public WebPage save(WebPage webPage) {
        return this.webPageRepository.save(webPage);
    }

    @Override
    public WebPage save(String id, LinkStatus status, Person person, String content) {
        return this.webPageRepository.save(new WebPage(id, status, person, content));
    }

    @Override
    public WebPage save(String id, LinkStatus status, String content) {
        return this.webPageRepository.save(new WebPage(id, status, content));
    }

    @Override
    public WebPage findById(Long id) {
        return this.webPageRepository.findById(id).orElseThrow(() -> new WebPageNotFoundException(id));
    }

    @Override
    public List<WebPage> listAll() {
        return this.webPageRepository.findAll();
    }

    @Override
    public WebPage deleteById(Long id) {
        WebPage webPage = this.webPageRepository.findById(id)
                .orElseThrow(() -> new WebPageNotFoundException(id));
        Person person = webPage.getPerson();
        person.getWebPages().remove(webPage);
        this.webPageRepository.delete(webPage);
        this.personRepository.save(person);
        return webPage;
    }

    @Override
    public WebPage moveLinkStatus(WebPage webPage) {
        // If the status was 'VALID', change the status to 'CRAWLED'; else, reverse the stuff
        webPage.setStatus(webPage.getStatus().equals(LinkStatus.CRAWLED) ? LinkStatus.VALID : LinkStatus.CRAWLED);
        this.save(webPage);
        return webPage;
    }
}
