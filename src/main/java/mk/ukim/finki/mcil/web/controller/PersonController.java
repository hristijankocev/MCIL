package mk.ukim.finki.mcil.web.controller;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;
import mk.ukim.finki.mcil.model.enums.LinkStatus;
import mk.ukim.finki.mcil.service.impl.PersonServiceImpl;
import mk.ukim.finki.mcil.service.impl.WebPageServiceImpl;
import mk.ukim.finki.mcil.service.impl.WorkplaceServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping(path = "/person")
public class PersonController {
    private final PersonServiceImpl personService;
    private final WebPageServiceImpl webPageService;
    private final WorkplaceServiceImpl workplaceService;

    public PersonController(PersonServiceImpl personService, WebPageServiceImpl webPageService, WorkplaceServiceImpl workplaceService) {
        this.personService = personService;
        this.webPageService = webPageService;
        this.workplaceService = workplaceService;
    }


    @GetMapping(path = "/add")
    public String getAddPersonPage(Model model) {
        model.addAttribute("workplaces", this.workplaceService.listAll());
        return "add-person";
    }

    @PostMapping(path = "/add")
    public String addPerson(@RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam(required = false) String worksAt,
                            @RequestParam(required = false) String validLinks,
                            @RequestParam(required = false) String crawledLinks,
                            Model model) {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            model.addAttribute("error", "Please fill in the required fields.");
            return "add-person";
        }

        Person person = this.personService.save(new Person(firstName, lastName));

        List<Workplace> worksAtList = new ArrayList<>();
        List<WebPage> crawledLinksList = new ArrayList<>();
        List<WebPage> validLinksList = new ArrayList<>();

        if (worksAt != null && !worksAt.isEmpty()) {
            for (String link : worksAt.trim().split(";")) {
                link = link.trim();
                worksAtList.add(this.workplaceService.save(link));
            }
        }

        if (validLinks != null && !validLinks.isEmpty()) {
            for (String link : validLinks.trim().split(";")) {
                link = link.trim();
                validLinksList.add(this.webPageService.save(link, LinkStatus.VALID, person, ""));
            }
        }

        if (crawledLinks != null && !crawledLinks.isEmpty()) {
            for (String link : crawledLinks.trim().split(";")) {
                link = link.trim();
                crawledLinksList.add(this.webPageService.save(link, LinkStatus.CRAWLED, person, ""));
            }
        }

        person.setWorksAtLinks(worksAtList);
        person.setValidLinks(validLinksList);
        person.setCrawledLinks(crawledLinksList);

        this.personService.save(person);

        return "redirect:/";
    }
}
