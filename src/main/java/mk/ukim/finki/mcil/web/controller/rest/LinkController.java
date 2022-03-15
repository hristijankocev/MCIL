package mk.ukim.finki.mcil.web.controller.rest;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.mcil.model.api.Link;
import mk.ukim.finki.mcil.model.exception.WebPageNotFoundException;
import mk.ukim.finki.mcil.service.api.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class LinkController {
    private final LinkService linkService;

    @GetMapping(value = "/webpage/preview", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> getPreview(@RequestParam Long linkId) {
        try {
            return new ResponseEntity<>(this.linkService.extractPreview(linkId), HttpStatus.OK);
        } catch (WebPageNotFoundException | IOException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        }
    }
}
