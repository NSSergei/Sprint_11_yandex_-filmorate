package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCreateRequest {
        String login;
        String name;
        String email;
        LocalDate birthday;
}
