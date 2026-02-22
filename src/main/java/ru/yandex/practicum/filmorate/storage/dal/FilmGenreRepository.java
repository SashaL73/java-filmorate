package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmGenreRowMapper;

import java.util.List;
import java.util.stream.Collectors;


@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenre> implements FilmGenreStorage {
    @Autowired
    FilmGenreRowMapper filmGenreRowMapper;

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

    public void saveFilmGenres(List<Object[]> batchArgs) {
        batchUpdate(INSERT_FILM_GENRE_QUERY,batchArgs);
    }

    public List<FilmGenre> genres(List<Long> filmsId) {
        String findGenresFilms = "SELECT fg.film_id, g.id AS genre_id, g.name AS GENRE_NAME " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRE g ON fg.GENRE_ID = g.ID " +
                "WHERE fg.FILM_ID IN (" + String.join(",", filmsId.stream().map(String::valueOf).collect(Collectors.toList())) + ")";
        return findMany(findGenresFilms,filmGenreRowMapper);
    }
}
