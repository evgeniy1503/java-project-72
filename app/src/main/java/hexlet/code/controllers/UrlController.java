package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;

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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class UrlController {

    public static Handler addUrl = ctx -> {

        String paramUrl = ctx.formParam("url");

        if (!isCorrectUrl(paramUrl)) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        String url = buildUrl(paramUrl);

        List<Url> list = new QUrl().findList();
        for (Url oldUrl : list) {
            if (oldUrl.getName().equals(url)) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "info");
                ctx.redirect("/urls");
                return;
            }
        }

        Url newUrl = new Url(url);
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    public static Handler listUrls = ctx -> {
        String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = 10;

        PagedList<Url> pagedUrls = new QUrl()
                .name.icontains(term)
                .setFirstRow(page * rowsPerPage)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

        int lastPage = pagedUrls.getTotalPageCount() + 1;
        int currentPage = pagedUrls.getPageIndex() + 1;

        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed()
                .collect(Collectors.toList());

        ctx.attribute("urls", urls);
        ctx.attribute("term", term);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("urls/index.html");
    };

    public static Handler showUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();
        List<UrlCheck> checkList = url.getUrlCheck();
        ctx.attribute("url", url);
        ctx.attribute("urlChecks", checkList);
        ctx.render("urls/show.html");
    };

    public static Handler checksUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);


        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (!haveConnect(url)) {
            ctx.sessionAttribute("flash", "Не корректный хост");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/urls/" + id);
            return;
        }

        String nameUrl = url.getName();

        UrlCheck urlCheck = buildUrlCheck(nameUrl);
        urlCheck.setUrl(url);
        urlCheck.save();

        System.out.println("-------------------->  " + url.getUrlCheck().size());

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + id);
    };

    private static boolean isCorrectUrl(String url) {
        UrlValidator urlValidator = UrlValidator.getInstance();
        return urlValidator.isValid(url);
    }

    private static String buildUrl(String url) throws MalformedURLException {
        URL fullUrl = new URL(url);
        return fullUrl.getProtocol() + "://" + fullUrl.getAuthority();
    }

    private static boolean haveConnect(Url url) throws IOException {

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

    private static UrlCheck buildUrlCheck(String nameUrl) {
        HttpResponse<String> httpResponse = Unirest
                .get(nameUrl)
                .asString();

        Integer statusCode = httpResponse.getStatus();

        String body = httpResponse.getBody();
        Document document = Jsoup.parse(body);

        Element titleTag = document.select("title").first();
        String title = titleTag != null ? titleTag.text() : "";

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
