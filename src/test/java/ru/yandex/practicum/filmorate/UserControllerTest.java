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
public class UserControllerTest {
    @LocalServerPort
    private int port;
    HttpClient client;

    @Test
    void getAllUsers() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void postUsers() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testName = """
                {
                  "email": "abc@mail.ru",
                  "login": "abc",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(response.body().contains("Sergei"));
    }

    @Test
    void PostWrongUsersLogin() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testName = """
                {
                  "email": "abc@mail.ru",
                  "login": "",
                  "name": "Sergei",
                  "birthday": 24.12.97;  
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        assertEquals(400, response.statusCode());
    }

    @Test
    void PostWrongUsersEmailIsEmpty() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testName = """
                {
                  "email": "abc.com",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": 24.12.97;  
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        assertEquals(400, response.statusCode());
    }

    @Test
    void PostWrongUsersEmail() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testName = """
                {
                  "email": "",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": 24.12.97;  
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        assertEquals(400, response.statusCode());
    }

    @Test
    void PostWrongUsersBirthday() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testName = """
                {
                  "email": "",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "2039-12-24";  
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        assertEquals(400, response.statusCode());
    }

    @Test
    void PostCorrectUsersEmail() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();

        String testName = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //System.out.println(response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    void PutCorrectTest() throws IOException, InterruptedException{
        client = HttpClient.newHttpClient();

        String testName = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        String updateTestName = """
                {
                  "id": 1,
                  "email": "sabc@mail.ru",
                  "login": "ZxCCxZ",
                  "name": "Sergei",
                  "birthday": "1980-09-24"
                }
                """;
        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateTestName))
                .build();

        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, putResponse.statusCode());
        assertTrue(putResponse.body().contains("ZxCCxZ"));
    }

    @Test
    void PutWrongTest() throws IOException, InterruptedException{
        client = HttpClient.newHttpClient();

        String testName = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        String updateTestName = """
                {
                  "id": 1,
                  "email": "sabc@mail.ru",
                  "login": "",
                  "name": "Sergei",
                  "birthday": "1980-09-24"
                }
                """;
        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateTestName))
                .build();

        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, putResponse.statusCode());
    }
}
