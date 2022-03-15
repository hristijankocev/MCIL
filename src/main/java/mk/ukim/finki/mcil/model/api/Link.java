package mk.ukim.finki.mcil.model.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Link {
    private String title;
    private String desc;
    private String ogUrl;
    private String ogTitle;
    private String ogDesc;
    private String ogImage;
    private String ogImageAlt;

    public Link(String title, String desc, String ogUrl, String ogTitle, String ogDesc, String ogImage, String ogImageAlt) {
        this.title = title;
        this.desc = desc;
        this.ogUrl = ogUrl;
        this.ogTitle = ogTitle;
        this.ogDesc = ogDesc;
        this.ogImage = ogImage;
        this.ogImageAlt = ogImageAlt;
    }
}
