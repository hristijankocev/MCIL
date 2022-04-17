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
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {

    private final WebPageRepository webPageRepository;
    private final ChromeDriver driver;

    @Override
    public Link extractPreview(Long linkId) {
        WebPage webPage = this.webPageRepository.findById(linkId).orElseThrow(() -> new WebPageNotFoundException(linkId));

        String url = webPage.getLink();

        driver.get(url);

        Document document = Jsoup.parse(driver.getPageSource());

        webPage.setContent(document.toString());
        this.webPageRepository.save(webPage);

        String title = getMetaTagContent(document, "meta[name=title]", "h1");
        String desc = getMetaTagContent(document, "meta[name=description]", "p");
        String ogUrl = StringUtils.defaultIfBlank(getMetaTagContent(document, "meta[property=og:url]", ""), url);
        String ogTitle = getMetaTagContent(document, "meta[property=og:title]", "h1");
        String ogDesc = getMetaTagContent(document, "meta[property=og:description]", "p");
        String ogImage = getMetaTagContent(document, "meta[property=og:image]", "img[src]");
        String ogImageAlt = getMetaTagContent(document, "meta[property=og:image:alt]", "");

        return new Link(title, desc, ogUrl, ogTitle, ogDesc, ogImage, ogImageAlt);
    }

    private String getMetaTagContent(Document document, String cssQuery, String alternateElQuery) {
        Element el = document.select(cssQuery).first();
        if (el != null) {
            return el.attr("content");
        }
        return getAlternateEl(document, alternateElQuery);
    }

    private String getAlternateEl(Document document, String query) {
        if (!query.isEmpty()) {
            if (query.equals("img[src]")) {
                Elements elements = document.select(query);
                for (Element el : elements) {
                    if (el != null) {
                        if (!el.attr("height").equals("") && Integer.parseInt(el.attr("height")) >= 180)
                            return el.attr("src");
                    }
                }
            } else {
                Element el = document.selectFirst(query);
                return el != null ? el.text() : "";
            }
        }
        return "";
    }
}
