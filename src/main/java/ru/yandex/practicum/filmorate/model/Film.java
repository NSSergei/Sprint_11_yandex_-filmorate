package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
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
    private Set<Long> likes = new HashSet<>();
}

