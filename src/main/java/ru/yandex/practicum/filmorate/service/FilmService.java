package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final static LocalDate FIRST_FILM = LocalDate.of(1895,12,28);

    public  FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        log.info("Request to add film: {}", film.getName());
        if (film.getReleaseDate() != null && isBefore(film.getReleaseDate(), FIRST_FILM)) {
            log.warn("Invalid ReleaseDate: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        log.info("Film added with id {} and name {}", film.getId(), film.getName());
        return filmStorage.addFilm(film);
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

        filmStorage.updateFilm(film);
        log.info("Film updates: id= {} and name= {}", film.getId(), film.getName());
        return  film;
    }

    public void deleteFilm(long id) {
        log.info("delete Film id{}",id);
        if (filmStorage.getFilmById(id).isEmpty()) {
            log.warn("Invalid delete id {} ",id);
            throw  new NotFoundException("Invalid id"); //!!!!!!!!!!
        }

        filmStorage.deleteFilm(id);
    }

    public Collection<Film> getFilms() {
        log.info("Request to get all films. Total films: {}", filmStorage.getFilms().size());
        return filmStorage.getFilms();
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

        film.getLikes().add(userId);
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
        film.getLikes().remove(userId);
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
        films.sort(new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                int firstFilmLikes = o1.getLikes().size();
                int secondFilmLikes = o2.getLikes().size();
                return Integer.compare(secondFilmLikes,firstFilmLikes);
            }
        });
        ArrayList<Film> top = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            top.add(films.get(i));
        }
        log.info("Top {} films list created successfully.", top.size());
        return top;
    }

    private boolean isBefore(LocalDate first, LocalDate second) {
        return  first.isBefore(second);
    }


}
