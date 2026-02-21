package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceDb {

    @Autowired
    @Qualifier("userRepository")
    private UserStorage userStorage;
    @Autowired
    private FriendshipRepository friendshipRepository;

    public List<UserDto> getUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + id + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    public UserDto createUser(NewUserRequest request) {
        Optional<User> alreadyExistUser = userStorage.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new ValidationException("Данный имейл уже используется");
        }

        User user = UserMapper.mapToUser(request);
        user = userStorage.saveUser(user);

        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = userStorage.getUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + request.getId() + " не найден"));
        updatedUser = userStorage.updateUser(updatedUser);
        log.info("Изменен пользовалеь: {}", updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public boolean addOrRemoveFriend(Long userId1, Long possibleFriendId, ActionType operation) {
        userStorage.getUserById(userId1)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId1 + " не найден"));
        userStorage.getUserById(possibleFriendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + possibleFriendId + " не найден"));
        Friendship friendship2 = friendshipRepository.findFriendship(possibleFriendId, userId1)
                .orElse(new Friendship());

        Friendship friendship1 = friendshipRepository.findFriendship(userId1, possibleFriendId)
                .orElse(new Friendship());


        if (operation.equals(ActionType.ADD)) {
            if (friendship1.getStatus() == null && friendship2.getStatus() == null) {
                friendshipRepository.addFriendRequest(userId1, possibleFriendId, "PENDING");
                return true;
            }

            if (friendship2.getStatus().equals("PENDING") && friendship1.getStatus() == null) {
                friendshipRepository.addFriendRequest(userId1, possibleFriendId, "CONFIRMED");
                friendshipRepository.confirmFriendships(possibleFriendId, userId1);
                return true;
            }


            if (friendship2.getStatus() != null && friendship2.getStatus().equals("CONFIRMED")) {
                log.error("Пользователи уже являеются друзьями");
                throw new ValidationException("Пользователи уже являеются друзьями");
            } else if (friendship1.getStatus() != null && friendship1.getStatus().equals("CONFIRMED")) {
                log.error("Пользователь уже принял запрос в друзья");
                throw new ValidationException("Пользователь уже принял запрос в друзья");
            }
            if (friendship1.getStatus() != null && friendship1.getStatus().equals("PENDING")) {
                log.error("Пользователь " + userId1 + " уже ожидает принятия дружбы c пользователем " + possibleFriendId);
                throw new ValidationException("Пользователь " +
                        userId1 + " уже ожидает принятия дружбы c пользователем " + possibleFriendId);
            }

        } else if (operation.equals(ActionType.DELETE)) {
            if (friendship1.getStatus() == null && friendship2.getStatus() == null) {
                return true;
            }

            if (friendship1.getStatus() != null && friendship1.getStatus().equals("PENDING") || Objects.equals(friendship1.getStatus(), "CONFIRMED")) {
                friendshipRepository.delete(userId1, possibleFriendId);
                return true;
            }

        }

        return true;
    }


    public List<UserDto> getListFriends(Long userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Польователь с ID = " + userId + " не найден"));

        return userStorage.findFriends(userId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }


    public List<UserDto> getCommonFriends(Long userId1, Long userId2) {
        userStorage.getUserById(userId1)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId1 + " не найден"));
        userStorage.getUserById(userId2)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId2 + " не найден"));

        return userStorage.findCommonFriends(userId1, userId2)
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();

    }

}
