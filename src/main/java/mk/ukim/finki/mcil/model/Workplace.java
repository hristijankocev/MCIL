package mk.ukim.finki.mcil.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Workplace {
    @Id
    private String name;

    @ManyToMany(mappedBy = "worksAtLinks")
    private List<Person> workers;

    public Workplace() {
    }

    public Workplace(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Person> workers) {
        this.workers = workers;
    }
}
