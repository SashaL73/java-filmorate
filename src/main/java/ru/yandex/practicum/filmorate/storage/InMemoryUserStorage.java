package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@Getter
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersMap = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public User saveUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        usersMap.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }


    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(usersMap.get(id));
    }

    @Override
    public User updateUser(User user) {
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<User> findFriends(long userId) {
        return List.of();
    }

    @Override
    public List<User> findCommonFriends(long userId1, long userId2) {
        return List.of();
    }

    private long getNextId() {
        long currentMaxId = usersMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
