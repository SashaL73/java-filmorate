package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dal.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmorateApplicationTestsDb {

    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final GenreRepository genreRepository;
    private final FriendshipRepository friendshipRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void initializeData() {
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");


        User userForSave1 = new User();
        userForSave1.setName("TestName");
        userForSave1.setLogin("TestLogin1");
        userForSave1.setEmail("test1@test.com");
        userForSave1.setBirthday(LocalDate.of(1998, 1, 1));
        userRepository.saveUser(userForSave1);

        User userForSave2 = new User();
        userForSave2.setName("TestName");
        userForSave2.setLogin("TestLogin2");
        userForSave2.setEmail("test2@test.com");
        userForSave2.setBirthday(LocalDate.of(2000, 1, 1));
        userRepository.saveUser(userForSave2);

        User userForSave3 = new User();
        userForSave3.setName("TestName");
        userForSave3.setLogin("TestLogin3");
        userForSave3.setEmail("test3@test.com");
        userForSave3.setBirthday(LocalDate.of(1992, 1, 1));
        userRepository.saveUser(userForSave3);

        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        Film filmForSave1 = new Film();
        filmForSave1.setName("Test");
        filmForSave1.setDescription("Test");
        filmForSave1.setReleaseDate(LocalDate.of(1999, 1, 1));
        filmForSave1.setDuration(120);
        filmForSave1.setMpa(mpa1);
        filmRepository.saveFilm(filmForSave1);

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        Film filmForSave2 = new Film();
        filmForSave2.setName("Test2");
        filmForSave2.setDescription("Test2");
        filmForSave2.setReleaseDate(LocalDate.of(1999, 1, 1));
        filmForSave2.setDuration(120);
        filmForSave2.setMpa(mpa2);
        filmRepository.saveFilm(filmForSave2);

        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        Film filmForSave3 = new Film();
        filmForSave3.setName("Test3");
        filmForSave3.setDescription("Test3");
        filmForSave3.setReleaseDate(LocalDate.of(2001, 1, 1));
        filmForSave3.setDuration(90);
        filmForSave3.setMpa(mpa3);
        filmRepository.saveFilm(filmForSave3);

    }

    @Test
    @Order(1)
    public void findFriendsAndFindCommonFriends() {

        friendshipRepository.addFriendRequest(1, 2, "PENDING");
        friendshipRepository.addFriendRequest(3, 2, "PENDING");

        List<User> friendsUserId1 = userRepository.findFriends(1);
        List<User> friendsUserId3 = userRepository.findFriends(3);

        assertThat(friendsUserId1)
                .hasSize(1)
                .anySatisfy(user -> assertThat(user.getId()).isEqualTo(2L));

        assertThat(friendsUserId3)
                .hasSize(1)
                .anySatisfy(user -> assertThat(user.getId()).isEqualTo(2L));

        List<User> commonFriendUser1AndUser2 = userRepository.findCommonFriends(1, 3);

        assertThat(commonFriendUser1AndUser2)
                .hasSize(1)
                .anySatisfy(user -> assertThat(user.getId()).isEqualTo(2L));

    }

    @Test
    @Order(2)
    public void findUserById() {
        Optional<User> userOptional = userRepository.getUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    @Order(3)
    public void findByEmail() {

        Optional<User> userOptional = userRepository.findByEmail("test1@test.com");
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "test1@test.com")
                );
    }


    @Test
    @Order(4)
    public void updateUser() {
        User userForUpdate = new User();
        userForUpdate.setId(1L);
        userForUpdate.setName("UpdatedName");
        userForUpdate.setLogin("UpdatedLogin1");
        userForUpdate.setEmail("Updatedtest1@test.com");
        userForUpdate.setBirthday(LocalDate.of(1996, 1, 1));

        userRepository.updateUser(userForUpdate);

        Optional<User> updatedUser = userRepository.getUserById(1);

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "Updatedtest1@test.com")
                                .hasFieldOrPropertyWithValue("login", "UpdatedLogin1")
                                .hasFieldOrPropertyWithValue("name", "UpdatedName")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1996, 1, 1))
                );
    }


    @Test
    @Order(5)
    public void findAllUser() {
        List<User> usersList = userRepository.findAll();

        assertThat(usersList)
                .hasSize(3);
    }

    @Test
    @Order(6)
    public void findFilmById() {

        Optional<Film> filmOptional = filmRepository.findFilmById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );

    }

    @Test
    @Order(7)
    public void findAllFilms() {
        List<Film> films = filmRepository.findAllFilms();

        assertThat(films)
                .hasSize(3);
    }

    @Test
    @Order(8)
    public void updateFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(3);

        Film updatedFilm = new Film();
        updatedFilm.setName("UpdatedName");
        updatedFilm.setDescription("UpdatedDescription");
        updatedFilm.setReleaseDate(LocalDate.of(1999, 1, 1));
        updatedFilm.setDuration(130);
        updatedFilm.setMpa(mpa);
        updatedFilm.setId(1L);

        filmRepository.updateFilm(updatedFilm);

        Optional<Film> film = filmRepository.findFilmById(1L);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("duration", 130)
                                .hasFieldOrPropertyWithValue("mpa", mpa)
                                .hasFieldOrPropertyWithValue("description", "UpdatedDescription")
                                .hasFieldOrPropertyWithValue("name", "UpdatedName")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1999, 1, 1))
                );


    }

    @Test
    @Order(9)
    public void findPopularFilms() {
        likeRepository.saveLike(1, 1);
        likeRepository.saveLike(2, 1);
        likeRepository.saveLike(3, 1);
        likeRepository.saveLike(1, 2);
        likeRepository.saveLike(2, 2);

        List<Film> films = filmRepository.findPopularFilm(2);

        assertThat(films)
                .hasSize(2)
                .element(0)
                .satisfies(film -> assertThat(film.getId()).isEqualTo(1L));
    }

    @Test
    @Order(10)
    public void findGenreByIdTest() {
        Optional<Genre> genreOptional = genreRepository.findGenreById(1L);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    @Order(11)
    public void findAllGenresTest() {
        List<Genre> genreList = genreRepository.findAllGenre();

        assertThat(genreList)
                .hasSize(6);
    }

    @Test
    @Order(12)
    public void findFilmGenreTest() {
        filmGenreRepository.saveFilmGenre(1L, 1L);
        List<Genre> genres = genreRepository.findFilmGenreById(1L);

        assertThat(genres)
                .hasSize(1)
                .element(0)
                .satisfies(genre -> assertThat(genre.getId()).isEqualTo(1L));
    }
}
