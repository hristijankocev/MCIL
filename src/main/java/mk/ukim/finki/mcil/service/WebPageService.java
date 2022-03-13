package mk.ukim.finki.mcil.service;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.enums.LinkStatus;

import java.util.List;

public interface WebPageService {
    WebPage save(WebPage webPage);

    WebPage save(String id, LinkStatus status, Person person, String content);

    WebPage save(String id, LinkStatus status, String content);

    WebPage findById(Long id);

    List<WebPage> listAll();

    WebPage deleteById(Long id);

    WebPage moveLinkStatus(WebPage webPage);
}
