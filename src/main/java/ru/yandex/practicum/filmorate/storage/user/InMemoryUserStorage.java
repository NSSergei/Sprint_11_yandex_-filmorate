package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dto.UserCreateRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userMap = new HashMap<>();

    // CRUD
    @Override
    public User addUser(User user) {
        user.setId(nextIndex());
        userMap.put(user.getId(), user);
        return user;
    }

    public void deleteUser(long id) {
        userMap.remove(id);
    }

    @Override
    public User updateUser(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return userMap.values();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public void userMapClear() {
        userMap.clear();
    }

    public long nextIndex() {
        long index = userMap.keySet().stream()
                .mapToLong(el -> el)
                .max()
                .orElse(0);
        return ++index;
    }
}
