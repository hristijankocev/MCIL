package mk.ukim.finki.mcil.model;

import mk.ukim.finki.mcil.model.enums.LinkStatus;

import javax.persistence.*;

@Entity
public class WebPage {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private LinkStatus status;

    @OneToOne
    private Person person;

    // Crawled content
    private String content;

    // fields: Map<String, String> - For later
}
