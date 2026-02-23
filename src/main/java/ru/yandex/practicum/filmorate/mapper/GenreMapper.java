package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreMapper {
    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto genreDto = new GenreDto();
        genreDto.setId(genre.getId());
        genreDto.setName(genre.getName());
        return genreDto;
    }

    public static Genre mapFilmGenreToGenre(FilmGenre filmGenre) {
        Genre genre = new Genre();
        genre.setId(filmGenre.getGenreId());
        genre.setName(filmGenre.getName());
        return genre;
    }
}
