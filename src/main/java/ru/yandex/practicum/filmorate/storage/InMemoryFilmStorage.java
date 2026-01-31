package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
@Getter
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmsMap = new HashMap<>();

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(filmsMap.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        filmsMap.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return filmsMap.get(id);
    }

    @Override
    public void replaceFilm(Film film) {
        filmsMap.put(film.getId(), film);
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
