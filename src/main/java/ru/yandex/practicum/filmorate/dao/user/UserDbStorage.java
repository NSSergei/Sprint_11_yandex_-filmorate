package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("userDateBaseRepository")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public User addUser(User user) {
        String sql = """
                INSERT INTO users (email, login,  name, birthday)
                VALUES(?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        User result = new User();
        result.setId(keyHolder.getKey().longValue());
        result.setEmail(user.getEmail());
        result.setLogin(user.getLogin());
        result.setName(user.getName());
        result.setBirthday(user.getBirthday());
        return result;
    }

    public User updateUser(User user) {
        String sql = """
                UPDATE users
                SET email = ?, login = ?,  name = ?, birthday = ?
                WHERE id = ?;
                """;

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    public void deleteUser(long id) {
        String sql = """
                DELETE
                FROM users
                WHERE id = ?
                """;
        jdbcTemplate.update("""
                DELETE
                FROM friendsLike
                WHERE userId = ? OR friendId = ?
                """, id, id);
        jdbcTemplate.update("""
                DELETE
                FROM filmsLike
                WHERE userId = ?
                """, id);
        jdbcTemplate.update(sql, id);
    }

    public Collection<User> getUsers() {
        String sql = """
                SELECT *
                FROM users
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public Optional<User> getUserById(long id) {
        String sql = """
                SELECT *
                FROM users
                WHERE id = ?
                """;
        List<User> user = jdbcTemplate.query(sql, new UserRowMapper(), id);
        return user.stream().findFirst();
    }

    public void userMapClear() {
        String sql = """
                DELETE
                FROM users
                """;
        jdbcTemplate.update(sql);
    }

    public void addFriend(long userId, long friendId) {
        String sql = """
                INSERT INTO friendsLike(userId, friendId)
                VALUES(?, ?);
                """;
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        String sql = """
                DELETE FROM friendsLike
                WHERE userId = ? AND friendId = ?
                """;
        jdbcTemplate.update(sql, userId, friendId);
    }

    public Collection<User> getAllFriends(long id) {
        String sql = """
                SELECT u.*
                FROM users AS u
                JOIN friendsLike AS fl ON u.id = fl.friendId
                WHERE fl.userId = ?
                ORDER BY u.id
                """;
        return jdbcTemplate.query(sql, new UserRowMapper(), id);
    }

    public Collection<User> checkMutualFriends(long firstId, long secondId) {
        String sql = """
                SELECT *
                FROM users
                WHERE id IN (
                    SELECT friendId
                    FROM friendsLike
                    WHERE userId = ?
                ) AND id IN (
                    SELECT friendId
                    FROM friendsLike
                    WHERE userId = ?
                )
                ORDER BY id
                """;
        return  jdbcTemplate.query(sql, new UserRowMapper(), firstId, secondId);
    }
}
