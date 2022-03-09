package mk.ukim.finki.mcil.service.impl;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.enums.LinkStatus;
import mk.ukim.finki.mcil.model.exception.WebPageNotFoundException;
import mk.ukim.finki.mcil.persistence.jpa.WebPageRepository;
import mk.ukim.finki.mcil.service.WebPageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebPageServiceImpl implements WebPageService {
    private final WebPageRepository webPageRepository;


    public WebPageServiceImpl(WebPageRepository webPageRepository) {
        this.webPageRepository = webPageRepository;
    }

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
    public Optional<WebPage> findById(String id) {
        WebPage webPage = this.webPageRepository.findById(id).orElseThrow(() -> new WebPageNotFoundException(id));
        return Optional.of(webPage);
    }

    @Override
    public List<WebPage> listAll() {
        return this.webPageRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        WebPage webPage = this.webPageRepository.findById(id).orElseThrow(() -> new WebPageNotFoundException(id));
        this.webPageRepository.deleteById(webPage.getId());
    }

    @Override
    public WebPage moveLinkStatus(WebPage webPage) {
        // If the status was 'VALID', change the status to 'CRAWLED'; else, reverse the stuff
        webPage.setStatus(webPage.getStatus().equals(LinkStatus.CRAWLED) ? LinkStatus.VALID : LinkStatus.CRAWLED);

        return webPage;
    }
}
