package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    User saveUser(User user);

    Optional<User> getUserById(long id);

    User updateUser(User user);

    Optional<User> findByEmail(String email);

    List<User> findFriends(long userId);

    List<User> findCommonFriends(long userId1, long userId2);


}
