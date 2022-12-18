package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class UrlController {

    public static Handler addUrl = ctx -> {
      String paramUrl = ctx.formParam("url");
      if (!(paramUrl.startsWith("https") || paramUrl.startsWith("http"))) {
          ctx.sessionAttribute("flash", "Некорректный URL");
          ctx.sessionAttribute("flash-type", "danger");
          ctx.redirect("/");
          return;
      }

      URL fullUrl = new URL(paramUrl);
      String url = fullUrl.getProtocol() + "://" + fullUrl.getAuthority();

      List<Url> list = new QUrl().findList();
      for (Url oldUrl : list) {
          if (oldUrl.getName().equals(url)) {
              ctx.sessionAttribute("flash", "Страница уже существует");
              ctx.sessionAttribute("flash-type", "success");
              ctx.redirect("/");
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

    };
}
