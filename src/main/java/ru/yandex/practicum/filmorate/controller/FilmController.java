package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {
        filmService.deleteFilm(id);
    }

    @PutMapping
    public Film changeInfo(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id,
                           @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable long id,
                              @PathVariable long userId) {
        filmService.addLikeToFilm(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilmsList(@RequestParam(defaultValue = "10") long count) {
        return filmService.getTopFilmsList(count);
    }
}
