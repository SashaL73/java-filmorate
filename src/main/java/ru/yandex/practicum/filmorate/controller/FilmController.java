package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ActionType;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Long id) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный Id");
        }
        return filmService.getFilm(id);
    }


    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            String errorMessage = "Id не должен быть пустым";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        return filmService.updateFilm(film);

    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLikeFilm(@PathVariable("id") Long id,
                               @PathVariable("userId") Long userId) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный Id");
        }

        if (userId == null || userId < 0) {
            throw new ValidationException("Некорректный Id");
        }

        return filmService.updatedLikeFilm(id, userId, ActionType.ADD);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLikeFilm(@PathVariable("id") Long id,
                                  @PathVariable("userId") Long userId) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный Id");
        }

        if (userId == null || userId < 0) {
            throw new ValidationException("Некорректный Id");
        }

        return filmService.updatedLikeFilm(id, userId, ActionType.DELETE);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", name = "count") int count) {
        if (count < 0) {
            throw new ValidationException("count должен быть больше нуля");
        }
        return filmService.getMostPopularFilms(count);
    }

}
