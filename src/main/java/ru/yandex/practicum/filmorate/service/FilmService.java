package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public  FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film addFilm(Film film) {
        log.info("Request to add film: {}", film.getName());

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Invalid name: {}", film.getName());
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Invalid descriptions size: {}",film.getDescription().length());
            throw new ValidationException(" максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() != null && isBefore(film.getReleaseDate(), LocalDate.of(1895,12,28))) {
            log.warn("Invalid ReleaseDate: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.warn("Invalid duration cant be negative : {}", film.getDuration());
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }

        log.info("Film added with id {} and name {}", film.getId(), film.getName());
        return inMemoryFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Invalid id is missing: {}", film.getId());
            throw new ValidationException("id фильма не указан");
        }

        if (!inMemoryFilmStorage.getFilmMap().containsKey(film.getId())) {
            log.warn("Film with  id {} not found", film.getId());
            throw new NotFoundException("Фильм с заданным id отсутствует " + film.getId() + " не найден");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Invalid name: {}", film.getName());
            throw new ValidationException("название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Invalid descriptions size: {}",film.getDescription().length());
            throw new ValidationException(" максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.warn("Invalid ReleaseDate: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");

        }

        if (film.getDuration() <= 0) {
            log.warn("Invalid duration cant be negative : {}", film.getDuration());
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }

        inMemoryFilmStorage.getFilmMap().put(film.getId(), film);
        log.info("Film updates: id= {} and name= {}", film.getId(), film.getName());
        return  film;
    }

    public void deleteFilm(long id) {
        log.info("delete Film id{}",id);
        if (!inMemoryFilmStorage.getFilmMap().containsKey(id)) {
            log.warn("Invalid delete id {} ",id);
            throw  new ValidationException("Invalid id");
        }

        inMemoryFilmStorage.deleteFilm(id);
    }

    public Collection<Film> getFilms() {
        log.info("Request to get all films. Total films: {}", inMemoryFilmStorage.getFilmMap().size());
        return inMemoryFilmStorage.getFilms();
    }

    public void addLikeToFilm(long filmId, long userId) {
        log.info("movies rating: userId {}, filmId {} ", userId, filmId);
        if (!inMemoryUserStorage.getUserMap().containsKey(userId)) {
            log.warn("Invalid userId {}, id not found in userMap", userId);
            throw new NotFoundException("Пользователь с данным id " + userId + " не найден");
        }

        if (!inMemoryFilmStorage.getFilmMap().containsKey(filmId)) {
            log.warn("Invalid filmId {}, id not found in filmMap", filmId);
            throw new NotFoundException("Фильм с данным id " + filmId + " не найден");
        }

        if (!inMemoryFilmStorage.getFilmsLikeInfoMap().containsKey(filmId)) {
            log.info("Add new HashSet<>()");
            inMemoryFilmStorage.getFilmsLikeInfoMap().put(filmId, new HashSet<>());
        }

        log.info("Add users like: id {}, for film id {}", userId, filmId);
        inMemoryFilmStorage.getFilmsLikeInfoMap().get(filmId).add(userId);
    }

    public void deleteLike(long filmId, long userId) {
        log.info("delete like: userId {}, filmId {} ", userId, filmId);
        if (!inMemoryUserStorage.getUserMap().containsKey(userId)) {
            log.warn("Invalid userId {}, id not found in userMap", userId);
            throw new NotFoundException("Пользователь с данным id " + userId + " не найден");
        }

        if (!inMemoryFilmStorage.getFilmMap().containsKey(filmId)) {
            log.warn("Invalid filmId {}, id not found in filmMap", filmId);
            throw new NotFoundException("Фильм с данным id " + filmId + " не найден");
        }

        log.info("Delete users like: id {}, for film id {}", userId, filmId);
        inMemoryFilmStorage.getFilmsLikeInfoMap().get(filmId).remove(userId);
    }

    public Collection<Film> getTopFilmsList(long count) {
        log.info("Start method getTopFilmsList: requested top size = {}", count);
        ArrayList<Film> films = new ArrayList<>(inMemoryFilmStorage.getFilms());

        if (films.size() < count) {
            log.warn("Requested top {} films, but only {} films are available. Returning all available films.",
                    count, films.size());
            count = films.size();
        }

        log.info("Sorting films by likes");
        films.sort(new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                int firs = inMemoryFilmStorage.getFilmsLikeInfoMap().getOrDefault(o1.getId(), new HashSet<>()).size();
                int second = inMemoryFilmStorage.getFilmsLikeInfoMap().getOrDefault(o2.getId(), new HashSet<>()).size();

                return Integer.compare(second, firs);
            }
        });
        ArrayList<Film> top = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            top.add(films.get(i));
        }
        log.info("Top {} films list created successfully.", top.size());
        return top;
    }

    public boolean isBefore(LocalDate first, LocalDate second) {
        return  first.isBefore(second);
    }

    public long nextIndex() {
        long index = inMemoryFilmStorage.getFilmMap().keySet().stream()
                .mapToLong(el -> el)
                .max()
                .orElse(0);
        return ++index;
    }
}
