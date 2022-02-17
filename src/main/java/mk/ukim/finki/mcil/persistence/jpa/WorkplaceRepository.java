package mk.ukim.finki.mcil.persistence.jpa;

import mk.ukim.finki.mcil.model.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, String> {
}
