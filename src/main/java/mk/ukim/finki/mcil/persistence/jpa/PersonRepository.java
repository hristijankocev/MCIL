package mk.ukim.finki.mcil.persistence.jpa;

import mk.ukim.finki.mcil.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
