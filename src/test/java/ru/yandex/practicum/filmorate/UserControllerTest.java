package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

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

    @Autowired
    private InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    void setUp() {
        client = HttpClient.newHttpClient();
        inMemoryUserStorage.getUserMap().clear();
        inMemoryUserStorage.getFriendsInfoMap().clear();
    }

    @Test
    void getAllUsers() throws IOException, InterruptedException {
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
    void postWrongUsersLogin() throws IOException, InterruptedException {
        String testName = """
                {
                  "email": "abc@mail.ru",
                  "login": "",
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
        assertEquals(400, response.statusCode());
    }

    @Test
    void postWrongUsersEmailIsEmpty() throws IOException, InterruptedException {
        String testName = """
                {
                  "email": "",
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
        assertEquals(400, response.statusCode());
    }

    @Test
    void postWrongUsersEmail() throws IOException, InterruptedException {
        String testName = """
                {
                  "email": "abc.com",
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
        assertEquals(400, response.statusCode());
    }

    @Test
    void postWrongUsersBirthday() throws IOException, InterruptedException {
        String testName = """
                {
                  "email": "",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "2039-12-24"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void postCorrectUsersEmail() throws IOException, InterruptedException {
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

        assertEquals(200, response.statusCode());
    }

    @Test
    void putCorrectTest() throws IOException, InterruptedException {
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
    void putWrongTest() throws IOException, InterruptedException {
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

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @Test
    void addFriend() throws IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();
        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        HttpRequest addFriendRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/2"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addFriendResponse = client.send(addFriendRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, addFriendResponse.statusCode());

    }

    @Test
    void addWrongIdFriend() throws  IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();

        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        HttpRequest addFriendRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addFriendResponse = client.send(addFriendRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, addFriendResponse.statusCode());

    }

    @Test
    void addWrongUserId() throws  IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();
        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        HttpRequest addFriendRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/5"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addFriendResponse = client.send(addFriendRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, addFriendResponse.statusCode());
    }

    @Test
    void deleteFriend() throws IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();
        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();
        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        HttpRequest addFriendRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/2"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> addFriendResponse = client.send(addFriendRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, addFriendResponse.statusCode());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/2"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());
    }

    @Test
    void deleteSameIdFriend() throws IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();

        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        HttpRequest addFriendRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/2"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addFriendResponse = client.send(addFriendRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, addFriendResponse.statusCode());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/1"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, deleteResponse.statusCode());
    }

    @Test
    void deleteWrongFriendId() throws IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();

        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        HttpRequest addFriendRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/2"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addFriendResponse = client.send(addFriendRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, addFriendResponse.statusCode());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/3"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, deleteResponse.statusCode());
    }

    @Test
    void getFriends() throws IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();

        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,getResponse.statusCode());
    }

    @Test
    void getFriendsWithWrongUser() throws IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();

        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/11/friends"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,getResponse.statusCode());
    }

    @Test
    void getCommonFriend() throws IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();

        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        String testName3 = """
                {
                  "email": "JKL@mail.ru",
                  "login": "YTIO",
                  "name": "Nick",
                  "birthday": "2002-12-24"
                }
                """;

        HttpRequest postRequest_3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName3))
                .build();

        HttpResponse<String> postResponse_3 = client.send(postRequest_3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_3.statusCode());

        HttpRequest add1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/3"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addOneResponse = client.send(add1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, addOneResponse.statusCode());

        HttpRequest add2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/2/friends/3"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addTwoResponse = client.send(add2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, addTwoResponse.statusCode());

        HttpRequest commonRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/common/2"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(commonRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,getResponse.statusCode());
        assertTrue(getResponse.body().contains("Nick"));
    }

    @Test
    void getCommonUsersId() throws IOException, InterruptedException {
        String testName1 = """
                {
                  "email": "sabc@mail.ru",
                  "login": "XcX",
                  "name": "Sergei",
                  "birthday": "1997-12-24"
                }
                """;

        HttpRequest postRequest_1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName1))
                .build();

        HttpResponse<String> postResponse_1 = client.send(postRequest_1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_1.statusCode());

        String testName2 = """
                {
                  "email": "xxx@mail.ru",
                  "login": "ZZZ",
                  "name": "Alex",
                  "birthday": "1980-12-24"
                }
                """;

        HttpRequest postRequest_2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName2))
                .build();

        HttpResponse<String> postResponse_2 = client.send(postRequest_2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_2.statusCode());

        String testName3 = """
                {
                  "email": "JKL@mail.ru",
                  "login": "YTIO",
                  "name": "Nick",
                  "birthday": "2002-12-24"
                }
                """;

        HttpRequest postRequest_3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(testName3))
                .build();

        HttpResponse<String> postResponse_3 = client.send(postRequest_3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse_3.statusCode());

        HttpRequest add1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/3"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addOneResponse = client.send(add1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, addOneResponse.statusCode());

        HttpRequest add2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/2/friends/3"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> addTwoResponse = client.send(add2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, addTwoResponse.statusCode());

        HttpRequest commonRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/users/1/friends/common/1"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(commonRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400,getResponse.statusCode());
    }
}
