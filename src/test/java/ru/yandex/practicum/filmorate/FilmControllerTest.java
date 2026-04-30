package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {
    @LocalServerPort
    private int port;
    HttpClient client;

    @Test
    void getAllFilms() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void PostFilm() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testFilm = """
                {
                  "name": "Matrix",
                  "description": "Sci-fi movie",
                  "releaseDate": "1999-03-31",
                  "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(response.body().contains("Matrix"));
    }

    @Test
    void PostWrongFilmsName() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testFilm = """
                {
                  "name": "",
                  "description": "Sci-fi movie",
                  "releaseDate": "1999-03-31",
                  "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        assertEquals(400, response.statusCode());
    }

    @Test
    void PostWrongLengthDescription() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testFilm = """
                {
                  "name": "Matrix",
                  "description": "Sci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fi
                  Sci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fi
                  Sci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-f
                  Sci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fi
                  Sci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fiSci-fi",
                  "releaseDate": "1999-03-31",
                  "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(400, response.statusCode());
    }

    @Test
    void PostWrongDate() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testFilm = """
                {
                  "name": "Matrix",
                  "description": "Sci-fi movie",
                  "releaseDate": "1895-12-28",
                  "duration": 120
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void PostCorrectDate() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testFilm = """
                {
                  "name": "Matrix",
                  "description": "Sci-fi movie",
                  "releaseDate": "1895-12-27",
                  "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void PostWrontDuration() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testFilm = """
                {
                  "name": "Matrix",
                  "description": "Sci-fi movie",
                  "releaseDate": "1999-03-31",
                  "duration": -120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void PostCorrectDuration() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testFilm = """
                {
                  "name": "Matrix",
                  "description": "Sci-fi movie",
                  "releaseDate": "1999-03-31",
                  "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void PutCorrectTest() throws IOException, InterruptedException{
        client = HttpClient.newHttpClient();

        String postFilm = """
                {
                 "name": "Matrix",
                 "description": "Sci-fi movie",
                 "releaseDate": "1999-03-31",
                 "duration": 120
                }
                """;
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postFilm))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        String putFilm = """
                {
                 "id": 1,
                 "name": "Matrix 2",
                 "description": "Sci-fi movie like Matrix 1",
                 "releaseDate": "1999-03-31",
                 "duration": 120
                }
                """;
        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(putFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, putResponse.statusCode());
        assertTrue(putResponse.body().contains("Matrix 2"));

    }

    @Test
    void PutWrongTest() throws IOException, InterruptedException{
        client = HttpClient.newHttpClient();

        String postFilm = """
                {
                 "name": "Matrix",
                 "description": "Sci-fi movie",
                 "releaseDate": "1999-03-31",
                 "duration": 120
                }
                """;
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postFilm))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        String putFilm = """
                {
                 "id": 1,
                 "name": "Matrix 2",
                 "description": "Sci-fi movie like Matrix 1",
                 "releaseDate": "1700-03-31",
                 "duration": 120
                }
                """;
        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(putFilm))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, putResponse.statusCode());
    }
}

