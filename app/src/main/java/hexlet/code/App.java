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
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.repository.BaseRepository;
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
        var classLoader = App.class.getClassLoader();
        var codeResolver = new ResourceCodeResolver("templates", classLoader);
        var templateEngine = TemplateEngine.create(codeResolver, Path.of("jte-classes"), ContentType.Html);
        return templateEngine;
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

        BaseRepository.setDataSource(dataSource);

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get("/", (ctx) -> ctx.html("Hello World"));

        return app;
    }

    public static void main(String[] args) throws SQLException, IOException {
        getApp().start(getPort());
    }
}
