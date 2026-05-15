package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.enums.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("filmDateBaseRepository")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = """
                INSERT INTO films (name, description, release_date, duration, mpa_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        saveGenres(film);
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        String sql = """
                DELETE FROM films
                WHERE id = ?
                """;
        deleteGenres(id);
        deleteLikes(id);
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        deleteGenres(film.getId());
        saveGenres(film);
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = """
                SELECT *
                FROM films
                ORDER BY id
                """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());
        films.forEach(this::loadGenres);
        return films;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        String sql = """
                SELECT *
                FROM films
                WHERE id = ?
                """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), id);

        films.forEach(this::loadGenres);
        return films.stream().findFirst();
    }

    @Override
    public void filmMapClear() {
        String sql = """
                DELETE
                FROM films
                """;
        jdbcTemplate.update("""
                DELETE
                FROM filmsGenre
                """);
        jdbcTemplate.update("""
                DELETE
                FROM filmsLike
                """);
        jdbcTemplate.update(sql);
    }

    public Collection<Film> getMpa(MpaRating mpa) {
        String sql = """
                SELECT *
                FROM films
                ORDER BY id
                """;
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());

        return films;
    }

    public Collection<Film> getFilmsByMpaId(Long id) {
        String sql = """
                SELECT *
                FROM films
                WHERE mpa_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, new FilmRowMapper(), id);
    }

    public Collection<Film> getFilmsByGenreId(Long id) {
        String sql = """
                SELECT f.*
                FROM films AS f
                JOIN filmsGenre AS fg ON f.id = fg.filmId
                WHERE fg.genreId = ?
                ORDER BY f.id
                """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), id);
        films.forEach(this::loadGenres);
        return films;
    }

    public  void addLikeToFilm(long filmId, long userId) {
        String sql = """
                INSERT INTO filmsLike(filmId, userId)
                VALUES (?,?)
                """;
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getTopFilmsList(long count) {
        String sql = """
            SELECT f.*,
                   COUNT(fl.userId) AS likes
            FROM films AS f
            LEFT JOIN filmsLike fl ON f.id = fl.filmId
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id
            ORDER BY likes DESC, f.id
            LIMIT ?
            """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), count);
        films.forEach(this::loadGenres);
        return films;
    }

    public void deleteLike(long filmId, long userId) {
        String sql = """
                DELETE
                FROM filmsLike
                WHERE filmId = ? AND userId = ?
                """;
        jdbcTemplate.update(sql, filmId, userId);
    }

    private void saveGenres(Film film) {
        String sql = """
                INSERT INTO filmsGenre(filmId, genreId)
                VALUES (?, ?)
                """;

        if (film.getGenres() == null) {
            return;
        }

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    private void deleteGenres(long filmId) {
        String sql = """
                DELETE
                FROM filmsGenre
                WHERE filmId = ?
                """;
        jdbcTemplate.update(sql, filmId);
    }

    private void deleteLikes(long filmId) {
        String sql = """
                DELETE
                FROM filmsLike
                WHERE filmId = ?
                """;
        jdbcTemplate.update(sql, filmId);
    }

    private void loadGenres(Film film) {
        String sql = """
                SELECT genreId
                FROM filmsGenre
                WHERE filmId = ?
                ORDER BY genreId
                """;

        List<Genre> genres = jdbcTemplate.query(sql,
                (rs, rowNum) -> Genre.fromId(rs.getLong("genreId")),
                film.getId());
        film.setGenres(new java.util.LinkedHashSet<>(genres));
    }
}
