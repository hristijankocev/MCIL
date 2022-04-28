package mk.ukim.finki.mcil.libraries.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebDriverLibrary {

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.87 Safari/537.36";

    @PostConstruct
    void postConstruct() {
        System.setProperty("webdriver.chrome.whitelistedIps", "");
    }

    @ConditionalOnProperty(
            value = "spring.profiles.active",
            havingValue = "prod")
    @Lazy
    @Bean
    public ChromeDriver getLocalChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless",
                "--disable-gpu",
                "--window-size=1920,1200",
                "--ignore-certificate-errors",
                "user-agent=" + USER_AGENT);
        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    @ConditionalOnProperty(
            value = "spring.profiles.active",
            havingValue = "docker-prod")
    @Bean
    public RemoteWebDriver getRemoteChromeDriver() {
        try {
            String remoteWebDriverUrl = "http://chromedriver:4444/wd/hub";
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless",
                    "--disable-gpu",
                    "--remote-debugging-port=9222",
                    "--no-sandbox",
                    "--whitelisted-ips=",
                    "--disable-dev-shm-usage",
                    "--window-size=1920,1200",
                    "--ignore-certificate-errors",
                    "user-agent=" + USER_AGENT);
            RemoteWebDriver driver = new RemoteWebDriver(new URL(remoteWebDriverUrl), options);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            return driver;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
