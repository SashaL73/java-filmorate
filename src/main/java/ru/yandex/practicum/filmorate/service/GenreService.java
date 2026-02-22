package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<GenreDto> findAllGenres() {
        return genreStorage.findAllGenre().stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDto findGenreById(long id) {
        Genre genre = genreStorage.findGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с ID: " + id + " не найден"));
        return GenreMapper.mapToGenreDto(genre);
    }

    public List<Genre> findGenre(long filmId) {
        return new ArrayList<>(genreStorage.findFilmGenreById(filmId));
    }

}
