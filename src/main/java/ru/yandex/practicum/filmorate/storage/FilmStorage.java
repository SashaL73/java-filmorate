package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAllFilms();

    Film saveFilm(Film film);

    Optional<Film> findFilmById(long id);

    Film updateFilm(Film film);

    List<Film> findPopularFilm(long limit);
}
