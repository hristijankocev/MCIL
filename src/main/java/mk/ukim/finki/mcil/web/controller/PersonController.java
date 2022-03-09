package mk.ukim.finki.mcil.web.controller;

import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;
import mk.ukim.finki.mcil.model.enums.LinkStatus;
import mk.ukim.finki.mcil.model.exception.PersonNotFoundException;
import mk.ukim.finki.mcil.model.exception.WebPageNotFoundException;
import mk.ukim.finki.mcil.model.exception.WorkplaceNotFoundException;
import mk.ukim.finki.mcil.service.impl.PersonServiceImpl;
import mk.ukim.finki.mcil.service.impl.WebPageServiceImpl;
import mk.ukim.finki.mcil.service.impl.WorkplaceServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                if (link.length() > 0)
                    worksAtList.add(this.workplaceService.save(link));
            }
        }

        if (validLinks != null && !validLinks.isEmpty()) {
            for (String link : validLinks.trim().split(";")) {
                link = link.trim();
                if (link.length() > 0)
                    validLinksList.add(this.webPageService.save(link, LinkStatus.VALID, person, ""));
            }
        }

        if (crawledLinks != null && !crawledLinks.isEmpty()) {
            for (String link : crawledLinks.trim().split(";")) {
                link = link.trim();
                if (link.length() > 0)
                    crawledLinksList.add(this.webPageService.save(link, LinkStatus.CRAWLED, person, ""));
            }
        }

        person.setWorksAtLinks(worksAtList);
        person.setValidLinks(validLinksList);
        person.setCrawledLinks(crawledLinksList);

        this.personService.save(person);

        return "redirect:/person/edit/" + person.getId();
    }

    @GetMapping(path = "/edit/{id}")
    public String editPerson(@PathVariable Long id,
                             @RequestParam(required = false) String editError,
                             Model model) {
        if (editError != null && !editError.isEmpty()) {
            model.addAttribute("editError", editError);
        }
        try {
            Person person = this.personService.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
            model.addAttribute("person", person);
        } catch (PersonNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "edit-person";
    }

    @DeleteMapping(path = "/{pid}/workplace/delete/{wid}")
    public String deleteWorkplaceForPerson(@PathVariable Long pid,
                                           @PathVariable String wid,
                                           RedirectAttributes redirectAttributes,
                                           Model model) {
        try {
            Person person = this.personService.findById(pid).orElseThrow(() -> new PersonNotFoundException(pid));
            person.removeWorkplace(this.personService.getWorkplace(person, wid)
                    .orElseThrow(() -> new WorkplaceNotFoundException(wid)));
            this.personService.save(person);
        } catch (PersonNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (WorkplaceNotFoundException e) {
            redirectAttributes.addAttribute("editError", e.getMessage());
        }
        return "redirect:/person/edit/" + pid;
    }

    @DeleteMapping(path = "/{pid}/link/delete")
    public String deleteLinkForPerson(@PathVariable Long pid,
                                      @RequestParam String linkId,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (linkId == null || linkId.isEmpty()) {
            redirectAttributes.addAttribute("editError", "Invalid link id.");
            return "redirect:/person/edit/" + pid;
        }
        try {
            Person person = this.personService.findById(pid).orElseThrow(() -> new PersonNotFoundException(pid));
            person.removeLink(this.webPageService.findById(linkId)
                    .orElseThrow(() -> new WebPageNotFoundException(linkId)));
            this.webPageService.deleteById(linkId);
            this.personService.save(person);
        } catch (PersonNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (WebPageNotFoundException e) {
            redirectAttributes.addAttribute("editError", e.getMessage());
        }
        return "redirect:/person/edit/" + pid;
    }

    @PostMapping(path = "/link/move")
    public String changeLinkStatus(@RequestParam String linkId,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (linkId == null || linkId.isEmpty()) {
            redirectAttributes.addAttribute("editError", "No link id specified");
            System.out.println(redirectAttributes.getAttribute("editError"));
        }
        try {
            WebPage webPage = this.webPageService.findById(linkId).orElseThrow(() -> new WebPageNotFoundException(linkId));
            webPage = this.webPageService.moveLinkStatus(webPage);
            this.webPageService.save(webPage);

            // TODO: Update one-to-many tables for the link<-->person tables

            return "redirect:/person/edit/" + webPage.getPerson().getId().toString();
        } catch (WebPageNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping(path = "/edit/{id}")
    public String updatePerson(@PathVariable String id,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam(required = false) String worksAt,
                               @RequestParam(required = false) String validLinks,
                               @RequestParam(required = false) String crawledLinks,
                               @RequestParam(required = false) String linkedinData,
                               @RequestParam(required = false) String facebookAbout,
                               RedirectAttributes redirectAttributes) {
        try {
            Person person = this.personService.findById(Long.parseLong(id))
                    .orElseThrow(() -> new PersonNotFoundException(Long.parseLong(id)));

            if (firstName == null || firstName.isEmpty()) {
                redirectAttributes.addAttribute("editError", "Please fill in the required fields.");
                return "redirect:/person/edit/" + id;
            } else {
                person.setFirstName(firstName);
            }

            if (lastName == null || lastName.isEmpty()) {
                redirectAttributes.addAttribute("editError", "Please fill in the required fields.");
                return "redirect:/person/edit/" + id;
            } else {
                person.setLastName(lastName);
            }

            if (facebookAbout != null && !facebookAbout.isEmpty()) {
                person.setFacebookAbout(facebookAbout);
            } else {
                person.setFacebookAbout("");
            }
            if (linkedinData != null && !linkedinData.isEmpty()) {
                person.setLinkedInData(linkedinData);
            } else {
                person.setLinkedInData("");
            }

            List<Workplace> worksAtList = person.getWorksAtLinks();
            List<WebPage> crawledLinksList = person.getCrawledLinks();
            List<WebPage> validLinksList = person.getValidLinks();

            if (worksAt != null && !worksAt.isEmpty()) {
                for (String link : worksAt.trim().split(";")) {
                    link = link.trim();
                    if (link.length() > 0)
                        worksAtList.add(this.workplaceService.save(link));
                }
            }
            if (validLinks != null && !validLinks.isEmpty()) {
                for (String link : validLinks.trim().split(";")) {
                    link = link.trim();
                    if (link.length() > 0)
                        validLinksList.add(this.webPageService.save(link, LinkStatus.VALID, person, ""));
                }
            }
            if (crawledLinks != null && !crawledLinks.isEmpty()) {
                for (String link : crawledLinks.trim().split(";")) {
                    link = link.trim();
                    if (link.length() > 0)
                        crawledLinksList.add(this.webPageService.save(link, LinkStatus.CRAWLED, person, ""));
                }
            }
            person.setValidLinks(validLinksList);
            person.setCrawledLinks(crawledLinksList);
            person.setWorksAtLinks(worksAtList);

            this.personService.save(person);
        } catch (PersonNotFoundException e) {
            redirectAttributes.addAttribute("editError", e.getMessage());
        }

        return "redirect:/person/edit/" + id;
    }
}
