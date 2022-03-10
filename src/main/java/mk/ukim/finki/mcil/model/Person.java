package mk.ukim.finki.mcil.model;

import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Lob
    private byte[] profilePicture;

    @ManyToMany
    @JoinTable(
            name = "person_works_at",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "workplace_id"))
    private List<Workplace> worksAtLinks;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "crawled_link_id"))
    private List<WebPage> crawledLinks;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "valid_link_id"))
    private List<WebPage> validLinks;

    private String facebookAbout;
    private String linkedInData;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date dateCreated;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_at")
    private Date modifyDate;

    public Person() {
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person(String firstName, String lastName, byte[] profilePicture, List<Workplace> worksAtLinks,
                  List<WebPage> crawledLinks, List<WebPage> validLinks, String facebookAbout, String linkedInData) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.worksAtLinks = worksAtLinks;
        this.crawledLinks = crawledLinks;
        this.validLinks = validLinks;
        this.facebookAbout = facebookAbout;
        this.linkedInData = linkedInData;
    }

    public void removeWorkplace(Workplace workplace) {
        this.worksAtLinks.remove(workplace);
        workplace.getWorkers().remove(this);
    }

    public void removeLink(WebPage webPage) {
        switch (webPage.getStatus()) {
            case CRAWLED: {
                this.crawledLinks.remove(webPage);
                break;
            }
            case VALID: {
                this.validLinks.remove(webPage);
                break;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Workplace> getWorksAtLinks() {
        return worksAtLinks;
    }

    public void setWorksAtLinks(List<Workplace> worksAtLinks) {
        this.worksAtLinks = worksAtLinks;
    }

    public List<WebPage> getCrawledLinks() {
        return crawledLinks;
    }

    public void setCrawledLinks(List<WebPage> crawledLinks) {
        this.crawledLinks = crawledLinks;
    }

    public List<WebPage> getValidLinks() {
        return validLinks;
    }

    public void setValidLinks(List<WebPage> validLinks) {
        this.validLinks = validLinks;
    }

    public String getFacebookAbout() {
        return facebookAbout;
    }

    public void setFacebookAbout(String facebookAbout) {
        this.facebookAbout = facebookAbout;
    }

    public String getLinkedInData() {
        return linkedInData;
    }

    public void setLinkedInData(String linkedInData) {
        this.linkedInData = linkedInData;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String generateBase64Image() {
        return Base64.encodeBase64URLSafeString(this.getProfilePicture());
    }
}
