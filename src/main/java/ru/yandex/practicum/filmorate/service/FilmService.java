package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final Comparator<Film> likeCountComparator = Comparator.comparing(Film::getLikesUsersId,
            (idSet1, idSet2) -> Integer.compare(idSet1.size(), idSet2.size()));

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.getFilmById(id);

        if (film == null) {
            throw new NotFoundException("Такого фильма нет");
        }
        return film;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.getFilmById(film.getId());

        if (updatedFilm == null) {
            String errorMessage = "Такого фильма нет";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

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

        filmStorage.replaceFilm(updatedFilm);
        log.info("Фильм изменен: {}", film);
        return updatedFilm;
    }


    public boolean updatedLikeFilm(Long idFilm, Long idUser, ActionType operation) {
        Film film = filmStorage.getFilmById(idFilm);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (userStorage.getUserById(idUser) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Long> idSet = film.getLikesUsersId();
        if (operation.equals(ActionType.ADD)) {
            idSet.add(idUser);
        } else {
            idSet.remove(idUser);
        }
        film.setLikesUsersId(idSet);

        filmStorage.replaceFilm(film);

        return true;
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(likeCountComparator.reversed())
                .limit(count)
                .toList();
    }


}
