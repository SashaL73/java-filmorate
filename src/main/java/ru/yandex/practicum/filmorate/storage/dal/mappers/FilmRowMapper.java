package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("mpa"));
        film.setMpa(mpa);
        LinkedHashSet<Genre> genreLinkedHashSet = new LinkedHashSet<>();
        if (resultSet.getString("genres") != null) {
            String genres = resultSet.getString("genres");
            String[] genreArray = genres.split(",");
            String genresIds = resultSet.getString("genre_ids");
            String[] idArray = genresIds.split(",");
            long[] genresId = new long[idArray.length];
            for (int i = 0; i < idArray.length; i++) {
                genresId[i] = Long.parseLong(idArray[i]);
            }
            for (int i = 0; i < genreArray.length; i++) {
                Genre genre = new Genre();
                genre.setId(genresId[i]);
                genre.setName(genreArray[i]);
                genreLinkedHashSet.add(genre);
            }
        }
        List<Genre> genreList = new ArrayList<>(genreLinkedHashSet);
        film.setGenres(genreList);
        return film;
    }
}
