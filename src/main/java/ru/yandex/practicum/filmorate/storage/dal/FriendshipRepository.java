package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.Optional;

@Repository
public class FriendshipRepository extends BaseRepository<Friendship> implements FriendshipStorage {
    private static final String INSERT_REQUEST_FRIENDSHIP_QUERY = "INSERT INTO friendships (user_id, friend_id, status)" +
            "VALUES (?, ?, ?)";
    private static final String UPDATE_REQUEST_FIENDSHIP_QUERY = "UPDATE friendships SET status = 'CONFIRMED' WHERE user_id = ?" +
            " AND friend_id = ?";
    private static final String FIND_FRIENDSHIP_QUERY = "SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_FRIENDSHIP = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";

    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper, Friendship.class);
    }

    @Override
    public Optional<Friendship> findFriendship(long userId, long friendId) {
        return findOne(FIND_FRIENDSHIP_QUERY, userId, friendId);
    }

    public void addFriendRequest(long userId, long friendId, String status) {
        int rowsAffected = jdbc.update(INSERT_REQUEST_FRIENDSHIP_QUERY, userId, friendId, status);
        if (rowsAffected == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }

    }

    public void confirmFriendships(long userId, long friendId) {
        update(
                UPDATE_REQUEST_FIENDSHIP_QUERY,
                userId,
                friendId
        );
    }

    public void delete(long userId, long friendId) {
        update(DELETE_FRIENDSHIP, userId, friendId);
    }
}
