package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.ActionType;
import ru.yandex.practicum.filmorate.service.UserServiceDb;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceDb userServiceDb;

    public UserController(UserServiceDb userServiceDb) {
        this.userServiceDb = userServiceDb;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userServiceDb.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long id) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный Id");
        }
        return userServiceDb.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest request) {
        return userServiceDb.createUser(request);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequest request) {

        if (request.getId() < 1) {
            String errorMessage = "Id должен быть указан";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        return userServiceDb.updateUser(request);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public boolean addFriends(@PathVariable("id") final Long id,
                              @PathVariable("friendId") final Long friendId) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный Id");
        }

        if (friendId == null || friendId < 0) {
            throw new ValidationException("Некорректный Id");
        }

        if (friendId.equals(id)) {
            throw new ValidationException("Id не могут совпадать");
        }
        return userServiceDb.addOrRemoveFriend(id, friendId, ActionType.ADD);
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

        return userServiceDb.addOrRemoveFriend(id, friendId, ActionType.DELETE);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable("id") final Long id) {
        if (id < 0) {
            throw new ValidationException("Некорректный Id");
        }
        return userServiceDb.getListFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable("id") final Long id,
                                          @PathVariable("otherId") final Long otherId) {
        if (id < 0) {
            throw new ValidationException("Некорректный Id");
        }

        if (otherId < 0) {
            throw new ValidationException("Некорректный Id");
        }

        return userServiceDb.getCommonFriends(id, otherId);
    }


}
