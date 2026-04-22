package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class AppTest extends FileReadingTest {

    private Javalin app;

    private static MockWebServer mockWebServer;

    /**
     * Starts a mockwebserver and queues a response to the "GET /" request.
     *
     * @param response The expected server response.
     * @return The started webserver address.
     * @throws IOException
     */
    public static String mockWebserverUrl(MockResponse response) throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.enqueue(response);
        mockWebServer.start();

        return mockWebServer.url("/").toString();
    }

    /**
     * Shuts down a mockwebserver if started.
     *
     * @throws IOException
     */
    @AfterEach
    public void shutdownWebserver() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @BeforeEach
    public final void beforeEach() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.root());
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты");
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post(NamedRoutes.urlsRoot(), requestBody);
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
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
            assertThat(response.code()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.getCode());
            assertThat(response.body().string()).contains("Некорректный URL");
        });
    }

    @Test
    public void testCreateExistingUrl() throws SQLException {
        var url = new Url("https://www.example.com");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post(NamedRoutes.urlsRoot(), requestBody);
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
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
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(response.body().string()).contains("https://www.example.com");
        });
    }

    @Test
    public void testShowUrl() throws SQLException {
        var url = new Url("https://www.example.com");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsRoot(url.getId()));
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(response.body().string()).contains("https://www.example.com");
        });
    }

    @Test
    @SuppressWarnings(value = "checkstyle:MagicNumber")
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsRoot(99999));
            assertThat(response.code()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
        });
    }

    @Test
    public void testCheckUrl() throws SQLException, IOException {
        var page = mockWebserverUrl(new MockResponse()
                .setBody(readFixture("fixtures/testHtml.html"))
                .setResponseCode(HttpStatus.OK.getCode()));
        var url = new Url(page);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsUrlCheck(url.getId()));
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            var lastUrlCheck = UrlCheckRepository.findAllByUrlId(url.getId()).getLast();
            assertThat(lastUrlCheck.getH1()).isEqualTo("Heading 1");
            assertThat(lastUrlCheck.getTitle()).isEqualTo("Test HTML");
            assertThat(lastUrlCheck.getUrlId()).isEqualTo(1);
            assertThat(lastUrlCheck.getDescription()).isEqualTo("Test check url");
        });
    }

    @Test
    public void testCheckEmptyResponse() throws SQLException, IOException {
        var page = mockWebserverUrl(new MockResponse()
                .setBody("")
                .setResponseCode(HttpStatus.OK.getCode()));
        var url = new Url(page);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsUrlCheck(url.getId()));
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            var lastUrlCheck = UrlCheckRepository.findAllByUrlId(url.getId()).getLast();
            assertThat(lastUrlCheck.getStatusCode()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(lastUrlCheck.getH1()).isEqualTo("");
            assertThat(lastUrlCheck.getTitle()).isEqualTo("");
            assertThat(lastUrlCheck.getDescription()).isEqualTo("");
        });
    }

    /** @Test */
    public void testCheckBadReply() throws SQLException, IOException {
        var page = mockWebserverUrl(new MockResponse()
                .setBody("")
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.getCode()));
        var url = new Url(page);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsUrlCheck(url.getId()));
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(response.body().string()).contains("Произошла ошибка при проверке");
        });
    }

    /** @Test */
    public void testCheckNoReply() throws SQLException, IOException {
        var url = new Url("http://wrong.test");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsUrlCheck(url.getId()));
            assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(response.body().string()).contains("Произошла ошибка при проверке");
        });
    }
}
