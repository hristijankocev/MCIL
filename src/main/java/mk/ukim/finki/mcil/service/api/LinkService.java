package mk.ukim.finki.mcil.service.api;

import mk.ukim.finki.mcil.model.api.Link;

import java.io.IOException;

public interface LinkService {
    Link extractPreview(Long linkId) throws IOException;
}
