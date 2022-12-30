package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;

import io.ebean.Database;
import io.javalin.Javalin;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static Database database;

    private static MockWebServer mockWebServer;

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();

        mockWebServer = new MockWebServer();

        MockResponse mockResponse = new MockResponse()
                .setBody(Files.readString(Paths.get("./src/test/resources/FakePage.html"), StandardCharsets.UTF_8))
                .setResponseCode(400);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.start();



    }

    @AfterAll
    public static void afterAll() throws IOException {
        app.stop();
        mockWebServer.shutdown();
    }

    @BeforeEach
    void beforeEach() {
        database.script().run("/truncate.sql");
        database.script().run("/seed.sql");
    }


    @Test
    void testWelcomePage() {
        HttpResponse<String> response = Unirest
                .get(baseUrl)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void testListUrl() {
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body).contains("https://vk.com");
        assertThat(body).contains("https://ok.ru");
    }

    @Test
    void testShowUrl() {
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/1")
                .asString();

        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body).contains("https://vk.com");
        assertThat(body).doesNotContain("https://ok.ru");
    }

    @Test
    void testCreateCorrectUrl() {
        String url = "https://iq.ru";

        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", url)
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body).contains(url);
        assertThat(body).contains("Страница успешно добавлена");

        Url chekUrl = new QUrl()
                .name.equalTo(url)
                .findOne();

        List<Url> urlList = new QUrl().findList();

        assertThat(chekUrl).isNotNull();
        assertThat(chekUrl.getName()).isEqualTo(url);
        assertThat(urlList.size()).isEqualTo(3);

    }

    @Test
    void testCreateIncorrectUrl() {
        String url = "htps://iq.ru";

        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", url)
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/");

        HttpResponse<String> response = Unirest
                .get(baseUrl)
                .asString();

        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body).contains("Некорректный URL");
    }

    @Test
    void testCreateDoubleUrl() {
        String url = "https://ok.ru";

        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", url)
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();

        List<Url> urlList = new QUrl().findList();

        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body).contains(url);
        assertThat(body).contains("Страница уже существует");
        assertThat(urlList.size()).isEqualTo(2);
    }

    @Test
    void testCheckUrl() {

        String mockUrl = mockWebServer.url("/").toString();

        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", mockUrl)
                .asEmpty();

        List<Url> list = new QUrl().findList();

        assertThat(responsePost.getStatus()).isEqualTo(302);
        assertThat(list.size()).isEqualTo(3);

        HttpResponse responseCheck = Unirest
                .post(baseUrl + "/urls/3/checks")
                .asEmpty();

        assertThat(responseCheck.getStatus()).isEqualTo(302);

        HttpResponse<String> responseShow = Unirest
                .get(baseUrl + "/urls/3")
                .asString();

        Url url = new QUrl().name.iequalTo(mockUrl.substring(0, mockUrl.length() - 1))
                .findOne();

        UrlCheck urlCheck = url.getUrlCheck().get(0);

        assertThat(responseShow.getStatus()).isEqualTo(200);

        assertThat(urlCheck.getStatusCode()).isEqualTo(400);
        assertThat(urlCheck.getTitle()).isEqualTo("Test title");
        assertThat(urlCheck.getH1()).isEqualTo("Test H1");


    }

}
