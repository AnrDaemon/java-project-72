package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import hexlet.code.dto.RootPage;
import io.javalin.http.Context;

public class RootController {

    public static void index(Context ctx) {
        var page = new RootPage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setAlertType(ctx.consumeSessionAttribute("alert"));
        page.setUrl(ctx.consumeSessionAttribute("url"));
        ctx.render("index.jte", model("page", page));
    }
}
