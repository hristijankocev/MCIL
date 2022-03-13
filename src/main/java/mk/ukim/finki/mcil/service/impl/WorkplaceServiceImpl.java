package mk.ukim.finki.mcil.service.impl;

import mk.ukim.finki.mcil.model.Workplace;
import mk.ukim.finki.mcil.model.exception.WorkplaceNotFoundException;
import mk.ukim.finki.mcil.persistence.jpa.WorkplaceRepository;
import mk.ukim.finki.mcil.service.WorkplaceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkplaceServiceImpl implements WorkplaceService {
    private final WorkplaceRepository workplaceRepository;

    public WorkplaceServiceImpl(WorkplaceRepository workplaceRepository) {
        this.workplaceRepository = workplaceRepository;
    }

    @Override
    public List<Workplace> listAll() {
        return this.workplaceRepository.findAll();
    }

    @Override
    public Workplace findById(String id) {
        return this.workplaceRepository
                .findById(id)
                .orElseThrow(() -> new WorkplaceNotFoundException(id));
    }

    @Override
    public Workplace save(String name) {
        return this.workplaceRepository.save(new Workplace(name));
    }

    @Override
    public void deleteById(String id) {
        Workplace workplace = this.workplaceRepository
                .findById(id)
                .orElseThrow(() -> new WorkplaceNotFoundException(id));
        this.workplaceRepository.delete(workplace);
    }
}
