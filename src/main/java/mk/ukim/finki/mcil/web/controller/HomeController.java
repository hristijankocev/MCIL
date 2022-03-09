package mk.ukim.finki.mcil.web.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(path = "/")
public class HomeController {

    @GetMapping
    public String getIndexPage(@RequestParam(required = false) String searchQuery,
                               Model model) {

        // Check whether the query is empty or not
        if (searchQuery != null && !searchQuery.isEmpty()) {
            model.addAttribute("searchQuery", searchQuery);
            searchQuery = searchQuery.replaceAll(" ", "+");
            final String googleInitQuery = "https://www.google.com/search?q=";

            Map<String, String> queryResult = getQueryResult(googleInitQuery + searchQuery);

            model.addAttribute("hasQuery", true);
            model.addAttribute("queryResult", queryResult);
        }

        return "index";
    }

    public Map<String, String> getQueryResult(String connUrl) {
        Map<String, String> dict = new HashMap<>();

        try {
            // User agent matters, google outputs different results for different User-Agents
            final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.87 Safari/537.36";
            // Fetch the page
            final Document doc = Jsoup.connect(connUrl).userAgent(USER_AGENT).get();
            // Select and traverse the elements that contain the links and extract the info
            for (Element result : doc.select("div.yuRUbf > a")) {
                final String title = result.text();
                final String url = result.attr("href");

                // Store the result in a dictionary
                dict.put(title, url);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return dict;
    }
}
