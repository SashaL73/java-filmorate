package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Optional;

@Repository
public class LikeRepository extends BaseRepository<Like> implements LikeStorage {
    private static final String INSERT_LIKE_FILM_QUERY = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String FIND_LIKE_FILM_QUERY = "SELECT * FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String DELETE_LIKE_FILM_QUERY = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

    public LikeRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper, Like.class);
    }

    public Optional<Like> findLike(long userId, long filmId) {
        return findOne(FIND_LIKE_FILM_QUERY, userId, filmId);
    }

    public void saveLike(long userId, long filmId) {
        int rowsAffected = jdbc.update(INSERT_LIKE_FILM_QUERY, userId, filmId);

        if (rowsAffected == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }


    public void delete(long userId, long filmId) {
        update(DELETE_LIKE_FILM_QUERY, userId, filmId);
    }
}
