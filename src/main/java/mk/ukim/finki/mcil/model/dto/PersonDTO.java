package mk.ukim.finki.mcil.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PersonDTO {

    @NotEmpty(message = "Field required")
    private String firstName;
    @NotEmpty(message = "Field required")
    private String lastName;
    private String worksAt;
    private String validLinks;
    private String crawledLinks;
    private String linkedinData;
    private String facebookAbout;
}
