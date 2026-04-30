package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
    private final Map<Long, Film> filmMap = new HashMap<>();
    @Getter
    Map<Long, Set<Long>> filmsLikeInfoMap = new HashMap<>();

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

    public long nextIndex() {
        long index = filmMap.keySet().stream()
                .mapToLong(el -> el)
                .max()
                .orElse(0);
        return ++index;
    }
}

