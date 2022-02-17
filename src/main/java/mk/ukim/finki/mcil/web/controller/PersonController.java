package mk.ukim.finki.mcil.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/person")
public class PersonController {

    @GetMapping(path = "/add")
    public String getAddPersonPage() {
        return "add-person";
    }
}
