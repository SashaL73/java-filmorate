package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ActionType;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);

    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        if (user.getId() == null) {
            String errorMessage = "Id должен быть указан";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public boolean addFriends(@PathVariable("id") final Long id,
                              @PathVariable("friendId") final Long friendId) {
        if (id < 0 || id == null) {
            throw new ValidationException("Некорректный Id");
        }

        if (friendId < 0 || friendId == null) {
            throw new ValidationException("Некорректный Id");
        }

        if (friendId == id) {
            throw new ValidationException("Id не могут совпадать");
        }
        return userService.addOrRemoveFriend(id, friendId, ActionType.ADD);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFriends(@PathVariable("id") final Long id,
                                 @PathVariable("friendId") final Long friendId) {
        if (id < 0) {
            throw new ValidationException("Некорректный Id");
        }

        if (friendId < 0) {
            throw new ValidationException("Некорректный Id");
        }

        if (friendId.equals(id)) {
            throw new ValidationException("Id не могут совпадать");
        }

        return userService.addOrRemoveFriend(id, friendId, ActionType.DELETE);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") final Long id) {
        if (id < 0) {
            throw new ValidationException("Некорректный Id");
        }
        return userService.getListFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") final Long id,
                                       @PathVariable("otherId") final Long otherId) {
        if (id < 0) {
            throw new ValidationException("Некорректный Id");
        }

        if (otherId < 0) {
            throw new ValidationException("Некорректный Id");
        }

        return userService.getCommonFriends(id, otherId);
    }


}
