package mk.ukim.finki.mcil.service.api;

import mk.ukim.finki.mcil.model.api.Link;


public interface LinkService {
    Link extractPreview(Long linkId);
}
