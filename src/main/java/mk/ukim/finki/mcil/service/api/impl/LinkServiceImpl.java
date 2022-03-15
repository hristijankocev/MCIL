package mk.ukim.finki.mcil.service.api.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.mcil.model.WebPage;
import mk.ukim.finki.mcil.model.api.Link;
import mk.ukim.finki.mcil.model.exception.WebPageNotFoundException;
import mk.ukim.finki.mcil.persistence.jpa.WebPageRepository;
import mk.ukim.finki.mcil.service.api.LinkService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final WebPageRepository webPageRepository;

    @Override
    public Link extractPreview(Long linkId) throws IOException {
        String url = this.webPageRepository.findById(linkId)
                .orElseThrow(() -> new WebPageNotFoundException(linkId))
                .getLink();

        if (!url.startsWith("http")) {
            url = url.replaceFirst("https", "http");
        }
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.87 Safari/537.36";
        Document document = Jsoup.connect(url).userAgent(USER_AGENT).get();
        String title = getMetaTagContent(document, "meta[name=title]");
        String desc = getMetaTagContent(document, "meta[name=description]");
        String ogUrl = StringUtils.defaultIfBlank(getMetaTagContent(document, "meta[property=og:url]"), url);
        String ogTitle = getMetaTagContent(document, "meta[property=og:title]");
        String ogDesc = getMetaTagContent(document, "meta[property=og:description]");
        String ogImage = getMetaTagContent(document, "meta[property=og:image]");
        String ogImageAlt = getMetaTagContent(document, "meta[property=og:image:alt]");

        return new Link(title, desc, ogUrl, ogTitle, ogDesc, ogImage, ogImageAlt);
    }

    private String getMetaTagContent(Document document, String cssQuery) {
        Element el = document.select(cssQuery).first();
        if (el != null) {
            return el.attr("content");
        }
        return "";
    }
}
