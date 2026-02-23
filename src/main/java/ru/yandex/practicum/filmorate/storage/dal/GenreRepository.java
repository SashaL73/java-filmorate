package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> implements GenreStorage {
    @Autowired
    GenreRowMapper genreRowMapper;
    private static final String FIND_ALL_GENRE_QUERY = "SELECT * FROM genre";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_FILM_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?)";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    public List<Genre> findFilmGenreById(long id) {
        return findMany(FIND_FILM_GENRE_BY_ID_QUERY, genreRowMapper, id);
    }

    public List<Genre> findAllGenre() {
        return findMany(FIND_ALL_GENRE_QUERY, genreRowMapper);
    }

    public Optional<Genre> findGenreById(long id) {
        return findOne(FIND_GENRE_BY_ID_QUERY, id);
    }


}
