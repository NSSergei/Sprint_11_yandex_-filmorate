package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.MpaRating;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        filmStorage.filmMapClear();
        userStorage.userMapClear();

        film = new Film();
        film.setName("Matrix");
        film.setDescription("Sci-fi");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(120);
        film.setMpa(MpaRating.G);

        user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void testAddFilm() {
        Film savedFilm = filmStorage.addFilm(film);

        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm.getName()).isEqualTo("Matrix");
    }

    @Test
    void testGetFilmById() {
        Film savedFilm = filmStorage.addFilm(film);

        Optional<Film> foundFilm =
                filmStorage.getFilmById(savedFilm.getId());

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f.getName()).isEqualTo("Matrix"));
    }

    @Test
    void testGetFilms() {
        filmStorage.addFilm(film);

        assertThat(filmStorage.getFilms()).hasSize(1);
    }

    @Test
    void testUpdateFilm() {
        Film savedFilm = filmStorage.addFilm(film);

        savedFilm.setName("Matrix Reloaded");

        filmStorage.updateFilm(savedFilm);

        Optional<Film> updatedFilm =
                filmStorage.getFilmById(savedFilm.getId());

        assertThat(updatedFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f.getName())
                                .isEqualTo("Matrix Reloaded"));
    }

    @Test
    void testDeleteFilm() {
        Film savedFilm = filmStorage.addFilm(film);

        filmStorage.deleteFilm(savedFilm.getId());

        Optional<Film> deletedFilm =
                filmStorage.getFilmById(savedFilm.getId());

        assertThat(deletedFilm).isEmpty();
    }

    @Test
    void testGetFilmsByMpaId() {
        filmStorage.addFilm(film);

        assertThat(filmStorage.getFilmsByMpaId(1L))
                .hasSize(1);
    }

    @Test
    void testAddLikeToFilm() {
        Film savedFilm = filmStorage.addFilm(film);

        User savedUser = userStorage.addUser(user);

        filmStorage.addLikeToFilm(
                savedFilm.getId(),
                savedUser.getId()
        );

        assertThat(filmStorage.getTopFilmsList(10))
                .hasSize(1);
    }

    @Test
    void testDeleteLike() {
        Film savedFilm = filmStorage.addFilm(film);

        User savedUser = userStorage.addUser(user);

        filmStorage.addLikeToFilm(
                savedFilm.getId(),
                savedUser.getId()
        );

        filmStorage.deleteLike(
                savedFilm.getId(),
                savedUser.getId()
        );

        assertThat(filmStorage.getTopFilmsList(10))
                .hasSize(1);
    }

    @Test
    void testGetTopFilmsList() {
        Film savedFilm = filmStorage.addFilm(film);

        User savedUser = userStorage.addUser(user);

        filmStorage.addLikeToFilm(
                savedFilm.getId(),
                savedUser.getId()
        );

        assertThat(filmStorage.getTopFilmsList(10))
                .hasSize(1);
    }

    @Test
    void testFilmMapClear() {
        filmStorage.addFilm(film);

        filmStorage.filmMapClear();

        assertThat(filmStorage.getFilms()).isEmpty();
    }
}