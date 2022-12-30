package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.DB;

import io.ebean.Transaction;
import io.javalin.Javalin;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static Transaction transaction;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    // При использовании БД запускать каждый тест в транзакции -
    // является хорошей практикой
    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
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

        String url = "https://ok.ru";


        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls/2/checks")
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls/2");

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/2")
                .asString();

        String body = response.getBody();

        List<UrlCheck> urlCheckList = new QUrlCheck().findList();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body).contains("Страница успешно проверена");
        assertThat(urlCheckList.size()).isEqualTo(2);

    }
    @Test
    void testWithMockWeb() {

        MockWebServer server = new MockWebServer();

    }


}
