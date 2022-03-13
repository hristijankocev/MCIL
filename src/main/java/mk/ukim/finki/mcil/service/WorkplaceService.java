package mk.ukim.finki.mcil.service;

import mk.ukim.finki.mcil.model.Workplace;

import java.util.List;
import java.util.Optional;

public interface WorkplaceService {
    List<Workplace> listAll();

    Workplace findById(String id);

    Workplace save(String name);

    void deleteById(String id);
}
