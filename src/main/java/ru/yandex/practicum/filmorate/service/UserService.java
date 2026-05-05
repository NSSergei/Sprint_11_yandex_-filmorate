package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;


@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        log.info("Request to create user");
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Invalid login: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Invalid email: {}", user.getEmail());
            throw new ValidationException("Ошибка формата электронной почты / почта не должна быть пустой");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Invalid birthday: {}", user.getBirthday());
            throw  new ValidationException("Дата рождения не может быть в будущем");
        }

        log.info("User added with id {} and login {}",user.getId(), user.getLogin());

        userStorage.addUser(user);
        return user;
    }

    public void deleteUser(long id) {
        if (userStorage.getUserById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userStorage.deleteUser(id);
    }

    public User changeUserInfo(User user) {
        if (user.getId() == null) {
            log.warn("User id is missing: {}", user.getId());
            throw new ValidationException("Id отсутствие");
        }

        if (userStorage.getUserById(user.getId()).isEmpty()) {
            log.warn("User with id {} not found", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Invalid birthday: {}", user.getBirthday());
            throw  new ValidationException("Дата рождения не может быть в будущем");
        }

        log.info("User updated: id= {}, login= {}", user.getId(), user.getLogin());
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        log.info("Request to get all users. Total users: {}", userStorage.getUsers().size());
        return userStorage.getUsers();
    }

    public void addFriend(long userId, long friendId) {
        log.info("Request to add new Friend. User id {}, Friend id: {}", userId, friendId);
        if (userId == friendId) {
            log.warn("Пользователь не может добавить самого себя id пользователя{}, id друга{}", userId,
                    friendId);
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId + " не найден"));

        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с friendId " + friendId + " не найден"));

        log.info("Add new friend,for userId id {}, add newFriendId id{}", userId, friendId);
        user.getFriends().add(friendId);

        log.info("Add new friend,for friendId id {}, add userId id{}", friendId, userId);
        friend.getFriends().add(userId);

    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Request to delete Friend. User id {}, Friend id: {}", userId, friendId);
        if (userId == friendId) {
            log.warn("Нельзя удалить себя из своих друзей: id пользователя{}, id друга{}", userId, friendId);
            throw new ValidationException("Нельзя удалить самого себя из друзей");
        }

        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с friendId " + friendId + " не найден"));

        log.info("Delete friend, userId id{}, add friendId id{}", userId, friendId);
        user.getFriends().remove(friendId);

        log.info("Delete friend, friendId id{}, add userId id{}", friendId, userId);
        friend.getFriends().remove(userId);
    }

    public Collection<User> getAllFriends(long id) {
        log.info("Start getALLFriends for user: id{}", id);

        User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("id not found in userMap"));

        ArrayList<User> allFriends = new ArrayList<>();

        log.info("Start iteration.");
        for (Long friend : user.getFriends()) {
            userStorage.getUserById(friend)
                    .ifPresent(allFriends::add);
        }

        log.info("return friends List");
        return  allFriends;
    }

    public Collection<User> checkMutualFriends(long firstId, long secondId) {
        log.info("Start checkMutualFriends: firstId{}, secondId{}", firstId, secondId);
        User firstUser = userStorage.getUserById(firstId)
                .orElseThrow(() -> new NotFoundException("Пользователь с firstUser " + firstId + " не найден"));

        User secondUser = userStorage.getUserById(secondId)
                .orElseThrow(() -> new NotFoundException("Пользователь с firstUser " + secondId + " не найден"));

        if (firstUser == secondUser) {
            log.warn("Пользователь 1 и пользователь 2 совпадают: firstUser{}, secondUser{}", firstUser, secondUser);
            throw new ValidationException("Пользователь 1 и 2 совпадают");
        }

        ArrayList<User> mutualFriends = new ArrayList<>();

        for (Long friendId : firstUser.getFriends()) {
            if (secondUser.getFriends().contains(friendId)) {
                userStorage.getUserById(friendId)
                        .ifPresent(mutualFriends::add);
            }
        }
        log.info("return mutualFriends list");
        return mutualFriends;
    }
}
