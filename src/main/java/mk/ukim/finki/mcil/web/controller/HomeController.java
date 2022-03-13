package mk.ukim.finki.mcil.web.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.mcil.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/")
public class HomeController {

    private final PersonService personService;

    @GetMapping
    public String getIndexPage(Model model, @RequestParam(required = false) String error) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }
        model.addAttribute("people", this.personService.listAll());
        return "index";
    }
}
