package mk.ukim.finki.mcil.web.controller;

import mk.ukim.finki.mcil.service.impl.PersonServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(path = "/")
public class HomeController {
    private final PersonServiceImpl personService;

    public HomeController(PersonServiceImpl personService) {
        this.personService = personService;
    }

    @GetMapping
    public String getIndexPage(Model model) {
        model.addAttribute("people", this.personService.listAll());
        return "index";
    }
}
