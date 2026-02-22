package ru.yandex.practicum.filmorate.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.LikeRepository;

import java.util.*;
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

    @Transactional
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
        List<Object[]> batchArgs = genres.stream()
                .map(genre -> new Object[]{savedFilm.getId(),genre.getId()})
                .toList();
        filmGenreRepository.saveFilmGenres(batchArgs);
        return FilmMapper.mapToFilmDto(savedFilm);
    }

    ;


    public FilmDto findFilmById(Long id) {
        Film film = filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с таким ID не найден"));
        film.setGenres(genreService.findGenre(id));
        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        if (films != null) {
            List<FilmDto> filmDto = films.stream()
                    .peek(film -> film.setGenres(genreService.findGenre(film.getId())))
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
        List<Long> id = films.stream()
                .map(Film::getId)
                .toList();
        List<FilmGenre> filmGenres = filmGenreRepository.genres(id);
        Map<Long, List<FilmGenre>> genreMap = filmGenres.stream()
                .collect(Collectors.groupingBy(filmGenre -> ((FilmGenre) filmGenre).getFilmId()));

        for (Film film : films) {
            film.setGenres(genreMap.getOrDefault(film.getId(),Collections.emptyList())
                    .stream()
                    .map(GenreMapper::mapFilmGenreToGenre)
                    .toList());
        }


        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

}
