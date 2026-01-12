package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;

    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        if (user.getId() == null) {
            String errorMessage = "Id должен быть указан";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (!users.containsKey(user.getId())) {
            String errorMessage = "Пользователя " + user + " не существует";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        } else {
            boolean emailExist = users.values().stream()
                    .anyMatch(user1 -> !user1.getId().equals(user.getId()) && user1.getEmail().equals(user
                            .getEmail()));
            if (emailExist) {
                String errorMessage = "Этот имейл уже используется " + user.getEmail();
                log.error(errorMessage);
                throw new ValidationException(errorMessage);
            }
        }

        User updatedUser = users.get(user.getId());
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            updatedUser.setLogin(user.getLogin());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            updatedUser.setBirthday(user.getBirthday());
        }
        log.info("Изменен пользовалеь: {}", updatedUser);
        users.put(user.getId(), updatedUser);
        return updatedUser;


    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
