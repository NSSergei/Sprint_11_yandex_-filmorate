package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmMap = new HashMap<>();

    //CRUD
    @Override
    public Film addFilm(Film film) {
        film.setId(nextIndex());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        filmMap.remove(id);
    }

    @Override
    public Film updateFilm(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        return filmMap.values();
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(filmMap.get(id));
    }

    @Override
    public void filmMapClear() {
        filmMap.clear();
    }

    public long nextIndex() {
        long index = filmMap.keySet().stream()
                .mapToLong(el -> el)
                .max()
                .orElse(0);
        return ++index;
    }
}

