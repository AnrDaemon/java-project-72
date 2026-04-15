package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class AppTest extends FileReadingTest {

    private Javalin app;

    private static MockWebServer mockWebServer;

    @BeforeAll
    @SuppressWarnings("checkstyle:MagicNumber")
    public static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        var mockResponse = new MockResponse()
                .setBody(readFixture("fixtures/testHtml.html"))
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    public final void beforeEach() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.root());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты");
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post(NamedRoutes.urlsRoot(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");
            var url = UrlRepository.findByName("https://www.example.com").get();
            assertThat(url.getName()).isEqualTo("https://www.example.com");
        });
    }

    @Test
    public void testCreateIncorrectUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=htt:example";
            var response = client.post(NamedRoutes.urlsRoot(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты");
        });
    }

    @Test
    public void testCreateExistingUrl() throws SQLException {
        var url = new Url("https://www.example.com");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post(NamedRoutes.urlsRoot(), requestBody);
            assertThat(response.code()).isEqualTo(422);
            var count = UrlRepository.getEntities().size();
            assertThat(count).isEqualTo(1);
        });
    }

    @Test
    public void testShowUrls() throws SQLException {
        var url = new Url("https://www.example.com");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsRoot());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");
        });
    }

    @Test
    public void testShowUrl() throws SQLException {
        var url = new Url("https://www.example.com");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsRoot(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");
        });
    }

    @Test
    public void testUserNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsRoot(99999));
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testCheckUrl() throws SQLException {
        var page = mockWebServer.url("/").toString();
        var url = new Url(page);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsUrlCheck(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            var lastUrlCheck = UrlCheckRepository.findAllByUrlId(url.getId()).getLast();
            assertThat(lastUrlCheck.getH1()).isEqualTo("Heading 1");
            assertThat(lastUrlCheck.getTitle()).isEqualTo("Test HTML");
            assertThat(lastUrlCheck.getUrlId()).isEqualTo(1);
            assertThat(lastUrlCheck.getDescription()).isEqualTo("Test check url");
        });
    }
}
