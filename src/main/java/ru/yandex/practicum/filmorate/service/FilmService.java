package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.enums.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate FIRST_FILM = LocalDate.of(1895,12,28);
    private MpaRating mpaRating;
    private final FilmDbStorage filmDbStorage;

    public  FilmService(@Qualifier("filmDateBaseRepository") FilmDbStorage filmStorage, @Qualifier(
            "userDateBaseRepository") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmDbStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        log.info("Request to add film: {}", film.getName());
        if (film.getReleaseDate() != null && isBefore(film.getReleaseDate(), FIRST_FILM)) {
            log.warn("Invalid ReleaseDate: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        log.info("Film added with id {} and name {}", film.getId(), film.getName());
        return filmDbStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Invalid id is missing: {}", film.getId());
            throw new ValidationException("id фильма не указан");
        }

        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            log.warn("Film with  id {} not found", film.getId());
            throw new NotFoundException("Фильм с заданным id отсутствует " + film.getId() + " не найден");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM)) {
            log.warn("Invalid ReleaseDate: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }

        filmDbStorage.updateFilm(film);
        log.info("Film updates: id= {} and name= {}", film.getId(), film.getName());
        return  film;
    }

    public void deleteFilm(long id) {
        log.info("delete Film id{}",id);
        if (filmStorage.getFilmById(id).isEmpty()) {
            log.warn("Invalid delete id {} ",id);
            throw  new NotFoundException("Invalid id");
        }

        filmDbStorage.deleteFilm(id);
    }

    public Collection<Film> getFilms() {
        log.info("Request to get all films. Total films: {}", filmStorage.getFilms().size());
        return filmDbStorage.getFilms();
    }

    public Film getFilmById(long id) {
        return filmDbStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public void addLikeToFilm(long filmId, long userId) {
        log.info("movies rating: userId {}, filmId {} ", userId, filmId);
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Invalid userId {}, id not found in userMap", userId);
            throw new NotFoundException("Пользователь с данным id " + userId + " не найден");
        }

        log.info("Add users like: id {}, for film id {}", userId, filmId);

        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        filmDbStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        log.info("delete like: userId {}, filmId {} ", userId, filmId);
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Invalid userId {}, id not found in userMap", userId);
            throw new NotFoundException("Пользователь с данным id " + userId + " не найден");
        }

        log.info("Delete users like: id {}, for film id {}", userId, filmId);

        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        filmDbStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getTopFilmsList(long count) {
        log.info("Start method getTopFilmsList: requested top size = {}", count);
        ArrayList<Film> films = new ArrayList<>(filmStorage.getFilms());

        if (films.size() < count) {
            log.warn("Requested top {} films, but only {} films are available. Returning all available films.",
                    count, films.size());
            count = films.size();
        }

        log.info("Sorting films by likes");
        return filmDbStorage.getTopFilmsList(count);

    }

    public Collection<MpaRating> getMpa() {
        return  List.of(MpaRating.values());
    }

    public Collection<Genre> getGenres() {
        return  List.of(Genre.values());
    }

    public Genre getGenreById(long id) {
        return Genre.fromId(id);
    }

    public MpaRating getMpaById(long id) {
        return MpaRating.fromId(id);
    }

    public Collection<Film> getFilmsByGenreId(Long id ) {
        return filmDbStorage.getFilmsByGenreId(id);
    }

    private boolean isBefore(LocalDate first, LocalDate second) {
        return  first.isBefore(second);
    }
}
