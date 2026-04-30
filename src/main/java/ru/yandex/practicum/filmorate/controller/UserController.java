package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import project.exception.ValidationException;
import project.model.User;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> userMap = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User user){
        log.info("Request to create user {}", user.getLogin());
        //логин не может быть пустым и содержать пробелы;
        if(user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")){
            log.warn("Invalid login: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым");
        }

        if(user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }

        if(user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")){
            log.warn("Invalid email: {}", user.getEmail());
            throw new ValidationException("Ошибка формата электронной почты / почта не должна быть пустой");
        }

        if(user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())){
            log.warn("Invalid birthday: {}", user.getBirthday());
            throw  new ValidationException("Дата рождения не может быть в будущем");
        }

        user.setId(nextIndex());
        userMap.put(user.getId(),user);

        log.info("User added with id {} and login {}",user.getId(), user.getLogin());

        return user;
    }

    public long nextIndex(){
        long index = userMap.keySet().stream()
                .mapToLong(el -> el)
                .max()
                .orElse(0);
        return ++index;
    }

    @PutMapping
    public User changeUserInfo(@Valid @RequestBody User user){
        if(user.getId() == null){
            log.warn("User id is missing: {}", user.getId());
            throw new ValidationException("Id отсутствие");
        }

        if(!userMap.containsKey(user.getId())){
            log.warn("User with id {} not found", user.getId());
            throw new ValidationException("Человек с данным id отсутствует");
        }

        if(user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")){
            log.warn("Invalid login: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым");
        }

        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if(user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")){
            log.warn("Invalid email: {}", user.getEmail());
            throw new ValidationException("Ошибка формата электронной почты / почта не должна быть пустой");
        }

        if(user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())){
            log.warn("Invalid birthday: {}", user.getBirthday());
            throw  new ValidationException("Дата рождения не может быть в будущем");
        }

        userMap.put(user.getId(),user);

        log.info("User updated: id={}, login={}", user.getId(), user.getLogin());

        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers(){
        log.info("Request to get all users. Total users: {}", userMap.size());
        return userMap.values();
    }
}
