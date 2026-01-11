package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;

    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {

        if (!films.containsKey(film.getId())) {
            String errorMessage = "Такого фильма нет";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        Film updatedFilm = films.get(film.getId());

        if (film.getName() != null) {
            updatedFilm.setName(film.getName());
        }

        if (film.getReleaseDate() != null) {
            updatedFilm.setReleaseDate(film.getReleaseDate());
        }

        if (film.getDescription() != null) {
            updatedFilm.setDescription(film.getDescription());
        }

        if (film.getDuration() != null) {
            updatedFilm.setDuration(film.getDuration());
        }

        films.put(film.getId(), updatedFilm);
        log.info("Фильм изменен: {}", film);
        return updatedFilm;

    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
