package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.ToString;

import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.enums.MpaRating;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@ToString
public class Film {
    private static final int FILM_SIZE_DESCRIPTION = 200;
    @PositiveOrZero(message = "id не может быть отрицательным числом")
    Long id;
    @NotBlank(message = "Название не должно быть пустым")
    String name;
    @Size(max = FILM_SIZE_DESCRIPTION, message = "Максимальная длина 200 символов")
    String description;
    LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительным числом")
    int duration;
    @NotNull(message = "MPA рейтинг обязателен")
    private MpaRating mpa;
    private Set<Genre> genres = new LinkedHashSet<>();
}
