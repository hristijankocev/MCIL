package mk.ukim.finki.mcil.service;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.enums.LinkStatus;

import java.util.List;
import java.util.Optional;

public interface WebPageService {
    WebPage save(WebPage webPage);

    WebPage save(String id, LinkStatus status, Person person, String content);

    WebPage save(String id, LinkStatus status, String content);

    Optional<WebPage> findById(String id);

    List<WebPage> listAll();

    void deleteById(String id);

    WebPage moveLinkStatus(WebPage webPage);
}