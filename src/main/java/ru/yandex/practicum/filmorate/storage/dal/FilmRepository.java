package ru.yandex.practicum.filmorate.storage.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("filmRepository")
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private static final Logger logger = LoggerFactory.getLogger(FilmRepository.class);
    @Autowired
    FilmRowMapper filmRowMapper;
    private static final String FIND_ALL_FILMS_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa, " +
            "GROUP_CONCAT(g.ID) AS genre_ids, GROUP_CONCAT(g.name) AS genres " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.id " +
            "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.id " +
            "GROUP BY f.id";
    private static final String INSERT_FILM_QUERY = "INSERT INTO FILMS (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_POPULAR_FILMS_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa, " +
            "GROUP_CONCAT(g.ID) AS genre_ids, GROUP_CONCAT(g.name) AS genres " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.id " +
            "JOIN likes l ON f.id = l.film_id " +
            "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.id " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l.USER_ID ) DESC LIMIT ?";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name=?, description=?, release_date= ?," +
            "duration= ?, mpa_id= ?";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa, " +
            "GROUP_CONCAT(g.ID) AS genre_ids, GROUP_CONCAT(g.name) AS genres " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.id " +
            "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.id " +
            "WHERE f.id = ? " +
            "GROUP BY f.id";

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
    @Transactional
    public Film saveFilm(Film film) {
        logger.info("Transaction name: {}", TransactionSynchronizationManager.getCurrentTransactionName());
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
