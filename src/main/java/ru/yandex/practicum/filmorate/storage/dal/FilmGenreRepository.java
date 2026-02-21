package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;


@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenre> implements FilmGenreStorage {

    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper, FilmGenre.class);
    }


    public void saveFilmGenre(long filmId, long genreId) {
        int rowsAffected = jdbc.update(INSERT_FILM_GENRE_QUERY, filmId, genreId);

        if (rowsAffected == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
