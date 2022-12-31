package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;

import io.ebean.PagedList;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class UrlController {

    public static Handler addUrl = ctx -> {

        String normalizedUrl = getUrl(ctx.formParam("url"));

        if (normalizedUrl == null) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        Url url = new QUrl().name.iequalTo(normalizedUrl).findOne();
        if (url != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
            ctx.redirect("/urls");
        } else {
            Url newUrl = new Url(normalizedUrl);
            newUrl.save();
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls");
        }

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
                .urlCheck.fetch()
                .orderBy()
                    .urlCheck.createdAt.desc()
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

        if (url == null) {
            throw new NotFoundResponse();
        }

        try {
            HttpResponse<String> httpResponse = Unirest
                    .get(url.getName())
                    .asString();
            UrlCheck newUrlCheck = buildUrlCheck(httpResponse);
            url.getUrlCheck().add(newUrlCheck);
            url.save();
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Не корректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flash-type", "danger");
        }
        ctx.redirect("/urls/" + id);
    };

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
