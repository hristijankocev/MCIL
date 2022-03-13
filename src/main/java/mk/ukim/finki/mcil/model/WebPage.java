package mk.ukim.finki.mcil.model;

import lombok.Data;
import mk.ukim.finki.mcil.model.enums.LinkStatus;

import javax.persistence.*;

@Entity
@Data
public class WebPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;

    @Enumerated(EnumType.STRING)
    private LinkStatus status;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private Person person;

    // Crawled content
    private String content;

    // fields: Map<String, String> - For later

    public WebPage() {
    }

    public WebPage(String link, LinkStatus status, String content) {
        this.link = link;
        this.status = status;
        this.content = content;
    }

    public WebPage(String link, LinkStatus status, Person person, String content) {
        this.link = link;
        this.status = status;
        this.person = person;
        this.content = content;
    }
}
