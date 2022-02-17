package mk.ukim.finki.mcil.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Workplace {
    @Id
    private String name;

    @ManyToMany
    private List<Person> workers;
}
