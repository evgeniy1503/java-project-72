package hexlet.code.utils;

import hexlet.code.domain.UrlCheck;

import kong.unirest.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URL;

public class ProcessorUrl {
    public static String getUrl(String urlParam) throws MalformedURLException {
        if (!(urlParam.startsWith("https") || urlParam.startsWith("http"))) {
            return null;
        } else {
            URL fullUrl = new URL(urlParam);
            return fullUrl.getProtocol() + "://" + fullUrl.getAuthority();
        }
    }

    public static UrlCheck buildUrlCheck(HttpResponse<String> httpResponse) {

        Integer statusCode = httpResponse.getStatus();

        String body = httpResponse.getBody();

        Document document = Jsoup.parse(body);

        String title = document.title();

        Element h1Tag = document.select("h1").first();
        String h1 = h1Tag != null ? h1Tag.text() : "";

        Element metaTag = document.getElementsByAttributeValue("name", "description").first();
        String description = metaTag != null ? metaTag.attr("content") : "";

        return new UrlCheck(statusCode, title, h1, description);
    }
}
