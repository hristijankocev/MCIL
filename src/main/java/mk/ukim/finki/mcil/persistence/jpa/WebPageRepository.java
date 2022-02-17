package mk.ukim.finki.mcil.persistence.jpa;

import mk.ukim.finki.mcil.model.WebPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebPageRepository extends JpaRepository<WebPage, String> {
}
