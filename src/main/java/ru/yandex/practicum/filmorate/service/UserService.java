package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
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

        inMemoryUserStorage.addUser(user);
        return user;
    }

    public void deleteUser(long id) {
        if (!inMemoryUserStorage.getUserMap().containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        inMemoryUserStorage.deleteUser(id);
    }

    public User changeUserInfo(User user) {
        if (user.getId() == null) {
            log.warn("User id is missing: {}", user.getId());
            throw new ValidationException("Id отсутствие");
        }

        if (!inMemoryUserStorage.getUserMap().containsKey(user.getId())) {
            log.warn("User with id {} not found", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

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

        log.info("User updated: id= {}, login= {}", user.getId(), user.getLogin());
        return inMemoryUserStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        log.info("Request to get all users. Total users: {}", inMemoryUserStorage.getUserMap().size());
        return inMemoryUserStorage.getUsers();
    }

    public void addFriend(long userId, long newFriendId) {
        log.info("Request to add new Friend. User id {}, Friend id: {}", userId, newFriendId);

        if (!inMemoryUserStorage.getUserMap().containsKey(userId)) {
            log.warn("Invalid user id: {}", userId);
            throw new NotFoundException("Пользователь с userId " + userId + " не найден");
        }

        if (!inMemoryUserStorage.getUserMap().containsKey(newFriendId)) {
            log.warn("Invalid friend id: {}", newFriendId);
            throw new NotFoundException("Пользователь с newFriendId " + newFriendId + " не найден");
        }

        if (userId == newFriendId) {
            log.warn("Пользователь не может добавить самого себя id пользователя{}, id друга{}", userId,
                    newFriendId);
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        if (!inMemoryUserStorage.getFriendsInfoMap().containsKey(userId)) {
            log.info("Add new HashSet<>()");
            inMemoryUserStorage.getFriendsInfoMap().put(userId, new HashSet<>());
        }

        log.info("Add new friend,for userId id {}, add newFriendId id{}", userId, newFriendId);
        inMemoryUserStorage.getFriendsInfoMap().get(userId).add(newFriendId);

        log.info("Add new friend,for newFriendId id {}, add userId id{}", newFriendId, userId);
        inMemoryUserStorage.getFriendsInfoMap().get(newFriendId).add(userId);

    }

    public void deleteFriend(long userId, long newFriendId) {
        log.info("Request to delete Friend. User id {}, Friend id: {}", userId, newFriendId);

        if (!inMemoryUserStorage.getUserMap().containsKey(userId)) {
            log.warn("Invalid user id: {}", userId);
            throw new NotFoundException("Пользователь с userId " + userId + " не найден");
        }

        if (!inMemoryUserStorage.getUserMap().containsKey(newFriendId)) {
            log.warn("Invalid friend id: {}", newFriendId);
            throw new NotFoundException("Пользователь с newFriendId " + newFriendId + " не найден");
        }

        if (userId == newFriendId) {
            log.warn("Нельзя удалить себя из своих друзей: id пользователя{}, id друга{}", userId, newFriendId);
            throw new ValidationException("Нельзя удалить самого себя из друзей");
        }

        log.info("Delete friend,for userId id{}, add newFriendId id{}", userId, newFriendId);
        inMemoryUserStorage.getFriendsInfoMap().get(userId).remove(newFriendId);

        log.info("Add new Friend,for newFriendId id{}, add userId id{}", newFriendId, userId);
        inMemoryUserStorage.getFriendsInfoMap().get(newFriendId).remove(userId);
    }

    public Collection<User> getAllFriends(long id) {
        log.info("Start getALLFriends for user: id{}", id);

        if (!inMemoryUserStorage.getUserMap().containsKey(id)) {
            log.warn("User not found in UserMap");
            throw new NotFoundException("id not found in userMap");
        }

        ArrayList<User> allFriends = new ArrayList<>();
        Set<Long> friendsId = inMemoryUserStorage.getFriendsInfoMap().getOrDefault(id, new HashSet<>());

        log.info("Start iteration.");
        for (Long friends : friendsId) {
            User friend = inMemoryUserStorage.getUserMap().get(friends);
            if (friend != null) {
                allFriends.add(friend);
            }
        }

        log.info("return friends List");
        return  allFriends;
    }

    public Collection<User> checkMutualFriends(long firstUser, long secondUser) {
        log.info("Start checkMutualFriends: firstUserId{}, secondUserId{}", firstUser, secondUser);

        if (!inMemoryUserStorage.getUserMap().containsKey(firstUser)) {
            log.warn("Invalid user id: {}", firstUser);
            throw new NotFoundException("Пользователь с firstUser " + firstUser + " не найден");
        }

        if (!inMemoryUserStorage.getUserMap().containsKey(secondUser)) {
            log.warn("Invalid user id: {}", secondUser);
            throw new ValidationException("Second id is not found in userMap");
        }

        if (firstUser == secondUser) {
            log.warn("Пользователь 1 и пользователь 2 совпадают: firstUser{}, secondUser{}", firstUser, secondUser);
            throw new ValidationException("Пользователь 1 и 2 совпадают");
        }

        Set<Long> friendsOfFirst = inMemoryUserStorage.getFriendsInfoMap().getOrDefault(firstUser, new HashSet<>());
        Set<Long> friendsOfSecond = inMemoryUserStorage.getFriendsInfoMap().getOrDefault(secondUser,
                new HashSet<>());
        ArrayList<User> mutualFriends = new ArrayList<>();

        for (Long friendId : friendsOfFirst) {
            if (friendsOfSecond.contains(friendId)) {
                User user = inMemoryUserStorage.getUserMap().get(friendId);
                mutualFriends.add(user);
            }
        }
        log.info("return mutualFriends list");
        return mutualFriends;
    }
}
