package mk.ukim.finki.mcil.model.dto;

import lombok.Data;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.Workplace;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class EditPersonDTO {
    @NotEmpty(message = "Field required")
    private Long id;
    @NotEmpty(message = "Field required")
    private String firstName;
    @NotEmpty(message = "Field required")
    private String lastName;
    private List<Workplace> worksAt;
    private List<WebPage> validLinks;
    private List<WebPage> crawledLinks;
    private String linkedInData;
    private String facebookAbout;
    private String profilePicture;
}
