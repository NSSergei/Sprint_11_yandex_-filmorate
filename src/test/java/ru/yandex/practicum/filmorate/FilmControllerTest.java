package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;

import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {

    @LocalServerPort
    private int port;

    HttpClient client;

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        client = HttpClient.newHttpClient();
        filmStorage.filmMapClear();
        userStorage.userMapClear();
    }

    @Test
    void getAllFilms() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void postFilm() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(response.body().contains("Matrix"));
    }

    @Test
    void postWrongFilmsName() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void postWrongLengthDescription() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix\",\n" +
                        "  \"description\": \"Sci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiiiii\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void postWrongDate() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1895-12-27\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void postCorrectDate() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1895-12-28\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void postWrongDuration() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1895-12-27\",\n" +
                        "  \"duration\": -120\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void postCorrectDuration() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void putCorrectTest() throws IOException, InterruptedException {
        String postFilm =
                "{\n" +
                        "  \"name\": \"Matrix\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postFilm))
                .build();

        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        String putFilm =
                "{\n" +
                        "  \"id\": \"1\",\n" +
                        "  \"name\": \"Matrix 2\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(putFilm))
                .build();

        HttpResponse<String> putResponse =
                client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, putResponse.statusCode());
        assertTrue(putResponse.body().contains("Matrix 2"));
    }

    @Test
    void putWrongTest() throws IOException, InterruptedException {
        String postFilm =
                "{\n" +
                        "  \"name\": \"Matrix\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postFilm))
                .build();

        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        String putFilm =
                "{\n" +
                        "  \"id\": \"1\",\n" +
                        "  \"name\": \"Matrix 2\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1700-03-31\",\n" +
                        "  \"duration\": -120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(putFilm))
                .build();

        HttpResponse<String> putResponse =
                client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, putResponse.statusCode());
    }

    @Test
    void deleteFilm() throws IOException, InterruptedException {
        String putFilm =
                "{\n" +
                        "  \"name\": \"Matrix 2\",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(putFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, putResponse.statusCode());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
        assertEquals("[]", getResponse.body());
    }

    @Test
    void deleteWrongIdFilm() throws IOException, InterruptedException {
        String putFilm =
                "{\n" +
                        "  \"name\": \"Matrix \",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(putFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, putResponse.statusCode());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/2"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, deleteResponse.statusCode());
    }

    @Test
    void testAddLikeToFilm() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix \",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        String testName =
                "{\n" +
                        "  \"email\": \"abc@mail.ru\",\n" +
                        "  \"login\": \"abc\",\n" +
                        "  \"name\": \"Sergei\",\n" +
                        "  \"birthday\": \"1997-12-24\"\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());

        HttpRequest requestToTestLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1/like/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> likeResponse = client.send(requestToTestLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,likeResponse.statusCode());
    }

    @Test
    void testAddLikeToFilmWrongUser() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix \",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        String testName =
                "{\n" +
                        "  \"email\": \"abc@mail.ru\",\n" +
                        "  \"login\": \"abc\",\n" +
                        "  \"name\": \"Sergei\",\n" +
                        "  \"birthday\": \"1997-12-24\"\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());

        HttpRequest requestToTestLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/5/like/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> likeResponse = client.send(requestToTestLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,likeResponse.statusCode());

    }

    @Test
    void testAddLikeToInvalidFilmId() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix \",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        String testName =
                "{\n" +
                        "  \"email\": \"abc@mail.ru\",\n" +
                        "  \"login\": \"abc\",\n" +
                        "  \"name\": \"Sergei\",\n" +
                        "  \"birthday\": \"1997-12-24\"\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());

        HttpRequest requestToTestLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1/like/5"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> likeResponse = client.send(requestToTestLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,likeResponse.statusCode());
    }

    @Test
    void deleteLike() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix \",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        String testName =
                "{\n" +
                        "  \"email\": \"abc@mail.ru\",\n" +
                        "  \"login\": \"abc\",\n" +
                        "  \"name\": \"Sergei\",\n" +
                        "  \"birthday\": \"1997-12-24\"\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());

        HttpRequest requestToTestLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1/like/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> likeResponse = client.send(requestToTestLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,likeResponse.statusCode());

        HttpRequest requestToDeleteLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1/like/1"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> responseToDelete = client.send(requestToDeleteLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,responseToDelete.statusCode());
    }

    @Test
    void testDeleteLikeToFilmWrongUser() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix \",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        String testName =
                "{\n" +
                        "  \"email\": \"abc@mail.ru\",\n" +
                        "  \"login\": \"abc\",\n" +
                        "  \"name\": \"Sergei\",\n" +
                        "  \"birthday\": \"1997-12-24\"\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpRequest requestToTestLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1/like/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> likeResponse = client.send(requestToTestLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, likeResponse.statusCode());

        HttpRequest requestToDeleteLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/5/like/1"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> responseToDelete = client.send(requestToDeleteLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseToDelete.statusCode());
    }

    @Test
    void testDeleteLikeToInvalidFilmId() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix \",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());
        String testName =
                "{\n" +
                        "  \"email\": \"abc@mail.ru\",\n" +
                        "  \"login\": \"abc\",\n" +
                        "  \"name\": \"Sergei\",\n" +
                        "  \"birthday\": \"1997-12-24\"\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpRequest requestToTestLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1/like/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> likeResponse = client.send(requestToTestLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, likeResponse.statusCode());

        HttpRequest requestToDeleteLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1/like/5"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> responseToDelete = client.send(requestToDeleteLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseToDelete.statusCode());
    }

    @Test
    void getTopFilmsList() throws IOException, InterruptedException {
        String testFilm =
                "{\n" +
                        "  \"name\": \"Matrix \",\n" +
                        "  \"description\": \"Sci-fi movie\",\n" +
                        "  \"releaseDate\": \"1999-03-31\",\n" +
                        "  \"duration\": 120\n" +
                        "}";

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        String testName =
                "{\n" +
                        "  \"email\": \"abc@mail.ru\",\n" +
                        "  \"login\": \"abc\",\n" +
                        "  \"name\": \"Sergei\",\n" +
                        "  \"birthday\": \"1997-12-24\"\n" +
                        "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());

        HttpRequest requestToTestLike = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/1/like/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> likeResponse = client.send(requestToTestLike, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,likeResponse.statusCode());

        HttpRequest requestToTop = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films/popular?count=10"))
                .GET()
                .build();

        HttpResponse<String> responseToTop =
                client.send(requestToTop, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseToTop.statusCode());
    }
}

