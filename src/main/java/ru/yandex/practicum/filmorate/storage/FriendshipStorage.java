package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Optional;

public interface FriendshipStorage {

    Optional<Friendship> findFriendship(long userId, long friendId);

    void addFriendRequest(long userId, long friendId, String status);

    void confirmFriendships(long userId, long friendId);

    void delete(long userId, long friendId);

}
