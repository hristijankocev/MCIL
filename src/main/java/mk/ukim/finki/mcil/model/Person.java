package mk.ukim.finki.mcil.model;

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
    private List<Workplace> worksAtLinks;

    @OneToMany
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "crawled_link_id"))
    private List<WebPage> crawledLinks;

    @OneToMany
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
}
