package hexlet.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

public class App {

    private static int getPort() {
        return Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    }

    public static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project");
    }

    private static TemplateEngine createTemplateEngine() {
        if ("jar".equals(App.class.getResource("App.class").getProtocol())) {
            var codeResolver = new ResourceCodeResolver("templates", App.class.getClassLoader());
            return TemplateEngine.create(codeResolver, ContentType.Html);
        }

        var codeResolver = new DirectoryCodeResolver(Path.of("src/main/resources/templates").toAbsolutePath());
        return TemplateEngine.create(codeResolver, Path.of("jte-classes"), ContentType.Html);
    }

    public static Javalin getApp() throws SQLException, IOException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        var dataSource = new HikariDataSource(hikariConfig);

        // Schema initialization
        var url = App.class.getClassLoader().getResourceAsStream("schema.sql");
        try (var stream = new InputStreamReader(url)) {
            var sql = new BufferedReader(stream)
                    .lines().collect(Collectors.joining("\n"));
            try (var connection = dataSource.getConnection();
                    var statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }

        // Datasource push
        BaseRepository.setDataSource(dataSource);

        // Javalin server creation
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get(NamedRoutes.root(), RootController::index);

        app.post(NamedRoutes.urlsRoot(), UrlsController::create);
        app.get(NamedRoutes.urlsRoot(), UrlsController::index);
        app.get(NamedRoutes.urlsRoot("{id}"), UrlsController::show);
        app.post(NamedRoutes.urlsUrlCheck("{id}"), UrlsController::check);

        return app;
    }

    public static void main(String[] args) throws SQLException, IOException {
        getApp().start(getPort());
    }
}
