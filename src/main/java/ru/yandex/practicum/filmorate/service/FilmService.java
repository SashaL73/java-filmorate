package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService {

    @Autowired
    @Qualifier("InMemoryFilmStorage")
    private FilmStorage filmStorage;
    @Autowired
    @Qualifier("InMemoryUserStorage")
    private UserStorage userStorage;

    private final Comparator<Film> likeCountComparator = Comparator.comparing(Film::getLikesUsersId,
            (idSet1, idSet2) -> Integer.compare(idSet1.size(), idSet2.size()));


    public Film getFilm(long id) {
        return filmStorage.findFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public List<Film> getFilms() {
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.saveFilm(film);
    }

    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.findFilmById(film.getId())
                .orElseThrow(() -> new NotFoundException("Такого фильма нет"));


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

        filmStorage.updateFilm(updatedFilm);
        log.info("Фильм изменен: {}", film);
        return updatedFilm;
    }


    public boolean updatedLikeFilm(Long idFilm, Long idUser, ActionType operation) {
        Film film = filmStorage.findFilmById(idFilm)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        Set<Long> idSet = film.getLikesUsersId();
        if (operation.equals(ActionType.ADD)) {
            idSet.add(idUser);
        } else {
            idSet.remove(idUser);
        }
        film.setLikesUsersId(idSet);

        filmStorage.updateFilm(film);

        return true;
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findAllFilms()
                .stream()
                .sorted(likeCountComparator.reversed())
                .limit(count)
                .toList();
    }


}
