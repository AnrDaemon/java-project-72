package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.jsoup.Jsoup;

import hexlet.code.dto.UrlsIndexPage;
import hexlet.code.dto.UrlsShowPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.UrlParser;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;

public class UrlsController {

    /**
     * /urls/ POST action
     */
    public static void create(Context ctx) throws SQLException {
        String normalizedUrl;
        try {
            normalizedUrl = UrlParser.getNormalizedUrl(ctx.formParam("url"));
        } catch (MalformedURLException | URISyntaxException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("alert", "alert-danger");
            ctx.redirect(NamedRoutes.root());
            return;
        }

        var url = UrlRepository.findByName(normalizedUrl).orElse(null);
        if (url != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("alert", "alert-warning");
            ctx.sessionAttribute("url", url.getName());
        } else {
            url = new Url(normalizedUrl);
            UrlRepository.save(url);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("alert", "alert-success");
        }
        ctx.redirect(NamedRoutes.urlsRoot(url.getId()), HttpStatus.SEE_OTHER);
    }

    /**
     * /urls/ GET action
     */
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var checks = UrlCheckRepository.findAllLatestChecks();
        var page = new UrlsIndexPage(urls, checks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setAlertType(ctx.consumeSessionAttribute("alert"));

        ctx.render("urls/index.jte", model("page", page));
    }

    /**
     * /urls/{id} GET action
     */
    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var urlChecks = UrlCheckRepository.findAllByUrlId(id);
        var page = new UrlsShowPage(url, urlChecks);
        page.setName(url.getName());
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setAlertType(ctx.consumeSessionAttribute("alert"));
        ctx.render("urls/show.jte", model("page", page));
    }

    /**
     * /urls/{id}/check POST action
     */
    public static void check(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var check = new UrlCheck(urlId);
        try {
            var response = Unirest.get(url.getName() + "/").asString();
            check.setStatusCode(response.getStatus());
            var html = response.getBody();
            var document = Jsoup.parse(html);
            check.setTitle(document.title());
            var elementH1 = document.selectFirst("h1");
            var h1 = elementH1 == null ? "" : elementH1.text();
            check.setH1(h1);
            var elementDescription = document.selectFirst("meta[name=description]");
            var description = elementDescription == null ? "" : elementDescription.attr("content");
            check.setDescription(description);
            UrlCheckRepository.save(check);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("alert", "alert-success");
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("alert", "alert-danger");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("alert", "alert-danger");
        }
        ctx.redirect(NamedRoutes.urlsRoot(urlId), HttpStatus.SEE_OTHER);
    }
}
