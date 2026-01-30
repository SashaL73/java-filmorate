package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }


    public boolean addOrRemoveFriend(Long userId1, Long userId2, ActionType operation) {
        User user1 = userStorage.getUserById(userId1);
        User user2 = userStorage.getUserById(userId2);

        if (user1 == null) {
            throw new NotFoundException("Польователь с id = " + userId1 + " не найден");
        }

        if (user2 == null) {
            throw new NotFoundException("Польователь с id = " + userId2 + " не найден");
        }

        Set<Long> friends1 = user1.getFriends();
        Set<Long> friends2 = user2.getFriends();

        if (operation.equals(ActionType.ADD)) {
            if (friends1.contains(userId2)) {
                log.error("Пользователь в уже добавлен в друзья");
                throw new ValidationException("Пользователь в уже добавлен в друзья");
            }

            friends1.add(userId2);
            friends2.add(userId1);
        } else if (operation.equals(ActionType.DELETE)) {
            if (!friends1.contains(userId2)) {
                log.error("Пользователь не состоит в друзьях");
                //throw new ValidationException("Пользователь не состоит в друзьях");
            }
            friends1.remove(userId2);
            friends2.remove(userId1);
        }

        user1.setFriends(friends1);
        user2.setFriends(friends2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        return true;
    }


    public List<User> getListFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Польователь с id = " + userId + " не найден");
        }

        //User user = inMemoryUserStorage.getUsersMap().get(userId);

        List<User> friends = user.getFriends()
                .stream()
                .map(this::getUserById)
                .toList();
        return friends;
    }

    public User updateUser(User user) {
        User updatedUser = userStorage.getUserById(user.getId());
        if (updatedUser == null) {
            String errorMessage = "Пользователя " + user + " не существует";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            boolean emailExist = userStorage.getUsers().stream()
                    .anyMatch(user1 -> !user1.getId().equals(user.getId()) && user1.getEmail().equals(user
                            .getEmail()));
            if (emailExist) {
                String errorMessage = "Этот имейл уже используется " + user.getEmail();
                log.error(errorMessage);
                throw new ValidationException(errorMessage);
            }
        }
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
        userStorage.addUser(updatedUser);
        return updatedUser;
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = userStorage.getUserById(userId1);
        User user2 = userStorage.getUserById(userId2);
        if (user1 == null) {
            throw new NotFoundException("Польователь с id = " + userId1 + " не найден");
        }

        if (user2 == null) {
            throw new NotFoundException("Польователь с id = " + userId2 + " не найден");
        }

        Set<Long> friendsUser1 = user1.getFriends();
        Set<Long> friendsUser2 = user2.getFriends();

        friendsUser1.retainAll(friendsUser2);

        if (friendsUser1.isEmpty()) {
            return new ArrayList<>();
        } else {
            return friendsUser1.stream()
                    .map(this::getUserById)
                    .toList();

        }
    }

    private User getUserById(Long id) {
        return userStorage.getUserById(id);
    }
}
