package hexlet.code.utils;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProcessorUrl {
    public static boolean isCorrectUrl(String url) {
        UrlValidator urlValidator = UrlValidator.getInstance();
        return urlValidator.isValid(url);
    }

    public static String buildUrl(String url) throws MalformedURLException {
        URL fullUrl = new URL(url);
        return fullUrl.getProtocol() + "://" + fullUrl.getAuthority();
    }

    public static boolean haveConnect(Url url) throws IOException {

        boolean checkConnect = false;

        URL urlCorrect = new URL(url.getName());

        HttpURLConnection connection = (HttpURLConnection) urlCorrect.openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.connect();
            checkConnect = true;
        } catch (Exception e) {
            connection.disconnect();
        } finally {
            connection.disconnect();
        }

        return checkConnect;
    }

    public static UrlCheck buildUrlCheck(String nameUrl) {

        HttpResponse<String> httpResponse = Unirest
                .get(nameUrl)
                .asString();

        Integer statusCode = httpResponse.getStatus();

        String body = httpResponse.getBody();
        Document document = Jsoup.parse(body);

        String title = document.title();

        Element h1Tag = document.select("h1").first();
        String h1 = h1Tag != null ? h1Tag.text() : "";

        Element metaTag = document.getElementsByAttributeValue("name", "description").first();
        String description = metaTag != null ? metaTag.attr("content") : "";

        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setStatusCode(statusCode);
        urlCheck.setTitle(title);
        urlCheck.setH1(h1);
        urlCheck.setDescription(description);

        return urlCheck;
    }
}
