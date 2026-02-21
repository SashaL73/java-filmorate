package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
@Getter
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmsMap = new HashMap<>();

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(filmsMap.values());
    }

    @Override
    public Film saveFilm(Film film) {
        film.setId(getNextId());
        filmsMap.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return Optional.ofNullable(filmsMap.get(id));
    }


    @Override
    public Film updateFilm(Film film) {
        filmsMap.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> findPopularFilm(long limit) {
        return List.of();
    }

    private long getNextId() {
        long currentMaxId = filmsMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
