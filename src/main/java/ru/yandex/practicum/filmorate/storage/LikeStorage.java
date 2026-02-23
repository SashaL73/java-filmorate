package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Optional;

public interface LikeStorage {
    Optional<Like> findLike(long userId, long filmId);

    void saveLike(long userId, long filmId);

    void delete(long userId, long filmId);
}
