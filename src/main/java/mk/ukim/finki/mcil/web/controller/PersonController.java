package mk.ukim.finki.mcil.web.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.mcil.model.Person;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.dto.EditPersonDTO;
import mk.ukim.finki.mcil.model.dto.PersonDTO;
import mk.ukim.finki.mcil.model.exception.PersonNotFoundException;
import mk.ukim.finki.mcil.model.exception.WebPageNotFoundException;
import mk.ukim.finki.mcil.model.exception.WorkplaceNotFoundException;
import mk.ukim.finki.mcil.service.PersonService;
import mk.ukim.finki.mcil.service.WebPageService;
import mk.ukim.finki.mcil.service.WorkplaceService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping(path = "/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final WebPageService webPageService;
    private final WorkplaceService workplaceService;

    @GetMapping(path = "/add")
    public String addPerson(Model model) {
        model.addAttribute("workplacesToChooseFrom", this.workplaceService.listAll());
        return "add-person";
    }

    @PostMapping(path = "/add")
    public String addPerson(@Valid PersonDTO personDTO, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("error", "Please fill in the required fields.");
            return "add-person";
        }

        this.personService.addPerson(personDTO);

        return "redirect:/";
    }

    @GetMapping(path = "/edit/{id}")
    public String editPerson(@PathVariable Long id,
                             @RequestParam(required = false) String editError,
                             @RequestParam(required = false) String searchQuery,
                             Model model) {
        if (editError != null && !editError.isEmpty()) {
            model.addAttribute("editError", editError);
        }
        try {
            EditPersonDTO person = this.personService.convertToDTO(id);
            model.addAttribute("person", person);

            // Check whether the search query is empty or not
            if (searchQuery != null && !searchQuery.isEmpty()) {
                this.personService.getGoogleQueryResults(searchQuery, person);
                model.addAttribute("person", person);
                return "redirect:/person/edit/" + person.getId();
            }
        } catch (PersonNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "edit-person";
    }

    @PostMapping(path = "/edit/{id}")
    public String updatePerson(@PathVariable String id,
                               @Valid PersonDTO newPerson,
                               Errors errors,
                               RedirectAttributes redirectAttributes) {
        try {
            Person oldPerson = this.personService.findById(Long.parseLong(id));

            if (errors.hasErrors()) {
                redirectAttributes.addAttribute("editError", "Please fill in the required fields.");
                return "redirect:/person/edit/" + id;
            }

            this.personService.editPerson(newPerson, oldPerson);

        } catch (PersonNotFoundException e) {
            redirectAttributes.addAttribute("editError", e.getMessage());
        }

        return "redirect:/person/edit/" + id;
    }

    @DeleteMapping(path = "/{pid}/workplace/delete/{wid}")
    public String deleteWorkplaceForPerson(@PathVariable Long pid,
                                           @PathVariable String wid,
                                           Model model) {
        try {
            this.personService.removeWorkplace(pid, wid);
        } catch (PersonNotFoundException | WorkplaceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/person/edit/" + pid;
    }

    @DeleteMapping(path = "/link/delete")
    public String deleteLinkForPerson(@RequestParam Long linkId,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (linkId == null) {
            redirectAttributes.addAttribute("error", "Invalid link id.");
            return "redirect:/";
        }
        try {
            WebPage webPage = this.webPageService.deleteById(linkId);
            return "redirect:/person/edit/" + webPage.getPerson().getId().toString();
        } catch (PersonNotFoundException | WebPageNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping(path = "/link/move")
    public String changeLinkStatus(@RequestParam Long linkId,
                                   Model model) {
        if (linkId == null) {
            model.addAttribute("error", "No link id specified");
            return "redirect:/";
        }
        try {
            WebPage webPage = this.webPageService.moveLinkStatus(this.webPageService.findById(linkId));

            return "redirect:/person/edit/" + webPage.getPerson().getId().toString();
        } catch (WebPageNotFoundException | PersonNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }

    @DeleteMapping(path = "/delete/{pid}")
    public String deletePerson(@PathVariable Long pid, RedirectAttributes redirectAttributes) {
        try {
            Person person = this.personService.findById(pid);
            this.personService.deleteById(person.getId());
        } catch (PersonNotFoundException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping(path = "/edit/{pid}/pfp")
    public String changeProfilePicture(@PathVariable String pid,
                                       @RequestParam MultipartFile file,
                                       ModelMap model) {
        try {
            Person person = this.personService.changePfp(pid, file);
            EditPersonDTO personDTO = this.personService.convertToDTO(person.getId());
            model.addAttribute("person", personDTO);
        } catch (PersonNotFoundException | IOException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "edit-person";
    }

    @PostMapping(path = "/edit/{pid}/pfp/external")
    public String changeProfilePictureFromUrl(@PathVariable String pid,
                                              @RequestParam String pictureUrl,
                                              Model model) {
        try {
            Person person = this.personService.changePfpExternalSource(pid, pictureUrl);
            EditPersonDTO personDTO = this.personService.convertToDTO(person.getId());
            model.addAttribute("person", personDTO);
        } catch (PersonNotFoundException | IOException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "edit-person";
    }

    @ControllerAdvice
    @RequiredArgsConstructor
    public static class FileUploadExceptionAdvice {
        private final Environment env;

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        @ResponseBody
        @ResponseStatus(value = HttpStatus.FORBIDDEN)
        public String handleMaxSizeException() {
            return String.format("Maximum filesize of %s exceeded.", env.getProperty("spring.servlet.multipart.max-request-size"));
        }
    }

}
