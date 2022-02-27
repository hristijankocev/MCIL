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

    public WebPage() {
    }

    public WebPage(String id, LinkStatus status, String content) {
        this.id = id;
        this.status = status;
        this.content = content;
    }

    public WebPage(String id, LinkStatus status, Person person, String content) {
        this.id = id;
        this.status = status;
        this.person = person;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkStatus getStatus() {
        return status;
    }

    public void setStatus(LinkStatus status) {
        this.status = status;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
