package mk.ukim.finki.mcil.service.api.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.mcil.libraries.selenium.WebDriverLibrary;
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {

    private final WebPageRepository webPageRepository;
    private RemoteWebDriver driver;

    @Autowired
    private Environment env;

    @PostConstruct
    private void initDriver() {
        this.driver = getWebDriver();
    }

    @Override
    public Link extractPreview(Long linkId, String sessionKey, String sessionPassword) {
        // If in any way something went wrong with the WebDriver, test it and if it's not working, re-init it
        try {
            this.driver.getTitle();
        } catch (Exception e) {
            this.driver = getWebDriver();
        }

        WebPage webPage = this.webPageRepository.findById(linkId).orElseThrow(() -> new WebPageNotFoundException(linkId));

        String url = webPage.getLink();

        this.driver.get(url);

        if (sessionKey != null && sessionPassword != null && !sessionKey.isEmpty() && !sessionPassword.isEmpty()) {
            // Sometimes LinkedIn will let you access the page, so check if the profile was already opened
            if (this.driver.findElementsByXPath("//*[@id=\"main-content\"]/section[1]/div/section/section[1]/div/div[2]/div[1]/h1").isEmpty()) {
                // Authenticate the user to LinkedIn
                authLinkedIn(url, sessionKey, sessionPassword);
            }
        }

        Document document = Jsoup.parse(this.driver.getPageSource());

        // Restart the driver to clear the cookies
        restartWebDriver();

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

    private void authLinkedIn(String originalUrl, String sessionKey, String sessionPassword) {
        this.driver.findElementByXPath("//*[@id=\"main-content\"]/div/form/p/button").click();

        WebElement usernameInput = this.driver.findElementByXPath("//*[@id=\"session_key\"]");
        WebElement passwordInput = this.driver.findElementByXPath("//*[@id=\"session_password\"]");

        usernameInput.sendKeys(sessionKey);
        passwordInput.sendKeys(sessionPassword);

        this.driver.findElementByXPath("//*[@id=\"main-content\"]/div/div/div/form/button").click();

        this.driver.get(originalUrl);
    }

    private void restartWebDriver() {
        this.driver.manage().deleteAllCookies();
        this.driver.quit();
        this.driver = getWebDriver();
    }

    private RemoteWebDriver getWebDriver() {
        String activeEnv = env.getProperty("spring.profiles.active");
        switch (Objects.requireNonNull(activeEnv)) {
            case "prod": {
                System.out.println("prod env");
                return new WebDriverLibrary().getLocalChromeDriver();
            }
            case "docker-prod": {
                System.out.println("docker prod env");
                return new WebDriverLibrary().getRemoteChromeDriver();
            }
            default: {
                System.out.println("Unknown profile");
                return new WebDriverLibrary().getLocalChromeDriver();
            }
        }
    }
}
