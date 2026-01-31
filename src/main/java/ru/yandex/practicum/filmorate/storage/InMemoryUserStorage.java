package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@Getter
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersMap = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        usersMap.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }


    @Override
    public User getUserById(Long id) {
        return usersMap.get(id);
    }

    @Override
    public void addUser(User user) {
        usersMap.put(user.getId(), user);
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
