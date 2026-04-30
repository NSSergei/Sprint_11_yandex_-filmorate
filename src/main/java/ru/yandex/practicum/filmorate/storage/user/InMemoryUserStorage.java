package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Long, User> userMap = new HashMap<>();
    @Getter
    private final Map<Long, Set<Long>> friendsInfoMap = new HashMap<>();

    // CRUD
    @Override
    public User addUser(User user) {
        user.setId(nextIndex());
        userMap.put(user.getId(), user);
        friendsInfoMap.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public void deleteUser(long id) {
        userMap.remove(id);
        friendsInfoMap.remove(id);
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

    public long nextIndex() {
        long index = userMap.keySet().stream()
                .mapToLong(el -> el)
                .max()
                .orElse(0);
        return ++index;
    }
}
