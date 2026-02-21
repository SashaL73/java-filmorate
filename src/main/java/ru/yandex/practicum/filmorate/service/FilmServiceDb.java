package ru.yandex.practicum.filmorate.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.LikeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@NoArgsConstructor
public class FilmServiceDb {
    @Autowired
    @Qualifier("filmRepository")
    private FilmStorage filmStorage;
    @Autowired
    private GenreStorage genreStorage;
    @Autowired
    private MpaStorage mpaStorage;
    @Autowired
    @Qualifier("userRepository")
    private UserStorage userStorage;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private FilmGenreRepository filmGenreRepository;
    @Autowired
    private MpaService mpaService;
    @Autowired
    private GenreService genreService;

    public FilmDto crateFilm(NewFilmRequest newFilmRequest) {
        Mpa mpa = mpaStorage.findById(newFilmRequest.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Указанный рейтинг не существует"));

        List<Genre> genres = new ArrayList<>();
        if (newFilmRequest.getGenres() != null) {
            Set<Long> genreIds = newFilmRequest.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());

            genres = genreIds.stream()
                    .map(id -> genreStorage.findGenreById(id)
                            .orElseThrow(() -> new NotFoundException("Жанр с указанным ID не существует")))
                    .collect(Collectors.toList());

        }

        Film film = FilmMapper.mapToFilm(newFilmRequest, mpa, genres);
        Film savedFilm = filmStorage.saveFilm(film);
        List<Long> idGenres = genres.stream().map(Genre::getId).toList();
        for (long id : idGenres) {
            filmGenreRepository.saveFilmGenre(savedFilm.getId(), id);
        }

        return FilmMapper.mapToFilmDto(savedFilm);
    }

    ;


    public FilmDto findFilmById(Long id) {
        Film film = filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с таким ID не найден"));
        film.setGenres(genreService.findGenre(id));
        film.setMpa(mpaService.findMpa(film.getMpa().getId()));
        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        if (films != null) {
            List<FilmDto> filmDto = films.stream()
                    .peek(film -> film.setGenres(genreService.findGenre(film.getId())))
                    .peek(film -> film.setMpa(mpaService.findMpa(film.getMpa().getId())))
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
            return filmDto;
        } else {
            return new ArrayList<>();
        }

    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film updatedFilm = filmStorage.findFilmById(request.getId())
                .map(film -> FilmMapper.updateFilm(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм с таким ID не найден"));
        updatedFilm = filmStorage.updateFilm(updatedFilm);
        updatedFilm.setMpa(mpaService.findMpa(updatedFilm.getMpa().getId()));
        updatedFilm.setGenres(genreService.findGenre(updatedFilm.getId()));
        return FilmMapper.mapToFilmDto(updatedFilm);
    }


    public boolean updatedLikeFilm(Long idFilm, Long idUser, ActionType operation) {
        filmStorage.findFilmById(idFilm)
                .orElseThrow(() -> new NotFoundException("Фильм с таким ID не найден"));
        userStorage.getUserById(idUser)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден"));

        Optional<Like> like = likeRepository.findLike(idUser, idFilm);
        if (operation.equals(ActionType.ADD)) {

            if (like.isEmpty()) {
                likeRepository.saveLike(idUser, idFilm);
            }
        } else {

            if (like.isPresent()) {
                likeRepository.delete(idUser, idFilm);
            }
        }

        return true;
    }

    public List<FilmDto> getMostPopularFilms(long count) {
        List<Film> films = filmStorage.findPopularFilm(count);
        return films.stream()
                .peek(film -> film.setGenres(genreService.findGenre(film.getId())))
                .peek(film -> film.setMpa(mpaService.findMpa(film.getMpa().getId())))
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

}
