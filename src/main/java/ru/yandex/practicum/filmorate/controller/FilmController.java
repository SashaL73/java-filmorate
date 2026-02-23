package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.ActionType;
import ru.yandex.practicum.filmorate.service.FilmServiceDb;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmServiceDb filmServiceDb;

    public FilmController(FilmServiceDb filmServiceDb) {
        this.filmServiceDb = filmServiceDb;
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable("id") Long id) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный Id");
        }
        return filmServiceDb.findFilmById(id);
    }


    @GetMapping
    public List<FilmDto> getFilms() {
        return filmServiceDb.findAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addFilm(@Valid @RequestBody NewFilmRequest newFilmRequest) {
        return filmServiceDb.crateFilm(newFilmRequest);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        if (request.getId() < 1) {
            String errorMessage = "Некорректный ID";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        return filmServiceDb.updateFilm(request);

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

        return filmServiceDb.updatedLikeFilm(id, userId, ActionType.ADD);
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

        return filmServiceDb.updatedLikeFilm(id, userId, ActionType.DELETE);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10", name = "count") long count) {
        if (count < 0) {
            throw new ValidationException("count должен быть больше нуля");
        }
        return filmServiceDb.getMostPopularFilms(count);
    }

}
