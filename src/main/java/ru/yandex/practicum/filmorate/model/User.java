package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;


@Data
@ToString
public class User  {
    @PositiveOrZero(message = "id не может быть отрицательным числом")
    Long id;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должна содержать символ @")
    String email;
    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    String login;
    String name;
    LocalDate birthday;
}
