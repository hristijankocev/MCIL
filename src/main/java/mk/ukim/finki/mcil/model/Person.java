package mk.ukim.finki.mcil.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
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

    //    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinTable(inverseJoinColumns = @JoinColumn(name = "crawled_link_id"))
//    private List<WebPage> crawledLinks;
//
//    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinTable(inverseJoinColumns = @JoinColumn(name = "valid_link_id"))
//    private List<WebPage> validLinks;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "webpage_id"))
    private List<WebPage> webPages;

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

    public Person(String firstName, String lastName, byte[] profilePicture, List<Workplace> worksAtLinks,
                  List<WebPage> webPages, String facebookAbout, String linkedInData) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.worksAtLinks = worksAtLinks;
        this.webPages = webPages;
        this.facebookAbout = facebookAbout;
        this.linkedInData = linkedInData;
    }

    public String generateBase64Image() {
        return Base64.encodeBase64String(this.getProfilePicture());
    }
}
