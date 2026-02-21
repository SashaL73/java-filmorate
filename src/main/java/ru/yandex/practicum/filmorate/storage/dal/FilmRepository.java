package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("filmRepository")
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    @Autowired
    FilmRowMapper filmRowMapper;
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM films";
    private static final String INSERT_FILM_QUERY = "INSERT INTO FILMS (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_POPULAR_FILMS_QUERY = "SELECT f.* FROM FILMS f" +
            " JOIN LIKES l ON f.ID = l.FILM_ID GROUP BY f.ID ORDER BY COUNT(l.USER_ID) DESC LIMIT ?";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name=?, description=?, release_date= ?," +
            "duration= ?, mpa_id= ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    @Override
    public List<Film> findPopularFilm(long limit) {
        return findMany(FIND_POPULAR_FILMS_QUERY, filmRowMapper, limit);
    }

    @Override
    public List<Film> findAllFilms() {
        return findMany(FIND_ALL_FILMS_QUERY, filmRowMapper);
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return findOne(FIND_FILM_BY_ID_QUERY, id);
    }

    @Override
    public Film saveFilm(Film film) {
        long id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        update(
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        return film;
    }
}
