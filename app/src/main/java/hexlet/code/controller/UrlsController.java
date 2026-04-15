package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import hexlet.code.dto.UrlsIndexPage;
import hexlet.code.dto.UrlsShowPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.UrlParser;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;

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
        } else {
            var newUrl = new Url(normalizedUrl);
            UrlRepository.save(newUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("alert", "alert-success");
        }

        ctx.redirect(NamedRoutes.urlsRoot(), HttpStatus.SEE_OTHER);
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
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setAlertType(ctx.consumeSessionAttribute("alert"));
        ctx.render("urls/show.jte", model("page", page));
    }
}
