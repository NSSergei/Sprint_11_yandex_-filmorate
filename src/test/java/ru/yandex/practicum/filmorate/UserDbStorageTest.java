package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    private User user;

    @BeforeEach
    void setUp() {
        userStorage.userMapClear();

        user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void testAddUser() {
        User savedUser = userStorage.addUser(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@mail.ru");
    }

    @Test
    void testGetUserById() {
        User savedUser = userStorage.addUser(user);

        Optional<User> foundUser =
                userStorage.getUserById(savedUser.getId());

        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u.getLogin())
                                .isEqualTo("testLogin"));
    }

    @Test
    void testGetUsers() {
        userStorage.addUser(user);

        Collection<User> users = userStorage.getUsers();

        assertThat(users).hasSize(1);
    }

    @Test
    void testUpdateUser() {
        User savedUser = userStorage.addUser(user);

        savedUser.setName("Updated User");

        userStorage.updateUser(savedUser);

        Optional<User> updatedUser =
                userStorage.getUserById(savedUser.getId());

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u.getName())
                                .isEqualTo("Updated User"));
    }

    @Test
    void testDeleteUser() {
        User savedUser = userStorage.addUser(user);

        userStorage.deleteUser(savedUser.getId());

        Optional<User> deletedUser =
                userStorage.getUserById(savedUser.getId());

        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testAddFriend() {
        User firstUser = userStorage.addUser(user);

        User secondUser = new User();
        secondUser.setEmail("friend@mail.ru");
        secondUser.setLogin("friendLogin");
        secondUser.setName("Friend");
        secondUser.setBirthday(LocalDate.of(2001, 1, 1));

        User savedSecondUser = userStorage.addUser(secondUser);

        userStorage.addFriend(
                firstUser.getId(),
                savedSecondUser.getId()
        );

        Collection<User> friends =
                userStorage.getAllFriends(firstUser.getId());

        assertThat(friends).hasSize(1);
    }

    @Test
    void testDeleteFriend() {
        User firstUser = userStorage.addUser(user);

        User secondUser = new User();
        secondUser.setEmail("friend@mail.ru");
        secondUser.setLogin("friendLogin");
        secondUser.setName("Friend");
        secondUser.setBirthday(LocalDate.of(2001, 1, 1));

        User savedSecondUser = userStorage.addUser(secondUser);

        userStorage.addFriend(
                firstUser.getId(),
                savedSecondUser.getId()
        );

        userStorage.deleteFriend(
                firstUser.getId(),
                savedSecondUser.getId()
        );

        Collection<User> friends =
                userStorage.getAllFriends(firstUser.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    void testGetAllFriends() {
        User firstUser = userStorage.addUser(user);

        User secondUser = new User();
        secondUser.setEmail("friend@mail.ru");
        secondUser.setLogin("friendLogin");
        secondUser.setName("Friend");
        secondUser.setBirthday(LocalDate.of(2001, 1, 1));

        User savedSecondUser = userStorage.addUser(secondUser);

        userStorage.addFriend(
                firstUser.getId(),
                savedSecondUser.getId()
        );

        Collection<User> friends =
                userStorage.getAllFriends(firstUser.getId());

        assertThat(friends)
                .hasSize(1)
                .extracting(User::getLogin)
                .contains("friendLogin");
    }

    @Test
    void testCheckMutualFriends() {
        User firstUser = userStorage.addUser(user);

        User secondUser = new User();
        secondUser.setEmail("second@mail.ru");
        secondUser.setLogin("secondLogin");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(2001, 1, 1));

        User thirdUser = new User();
        thirdUser.setEmail("third@mail.ru");
        thirdUser.setLogin("thirdLogin");
        thirdUser.setName("Third");
        thirdUser.setBirthday(LocalDate.of(2002, 2, 2));

        User savedSecondUser = userStorage.addUser(secondUser);
        User savedThirdUser = userStorage.addUser(thirdUser);

        userStorage.addFriend(
                firstUser.getId(),
                savedThirdUser.getId()
        );

        userStorage.addFriend(
                savedSecondUser.getId(),
                savedThirdUser.getId()
        );

        Collection<User> mutualFriends =
                userStorage.checkMutualFriends(
                        firstUser.getId(),
                        savedSecondUser.getId()
                );

        assertThat(mutualFriends)
                .hasSize(1)
                .extracting(User::getLogin)
                .contains("thirdLogin");
    }

    @Test
    void testUserMapClear() {
        userStorage.addUser(user);

        userStorage.userMapClear();

        assertThat(userStorage.getUsers()).isEmpty();
    }
}