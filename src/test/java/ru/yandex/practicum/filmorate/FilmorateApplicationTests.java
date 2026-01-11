package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmorateApplicationTests {

    private Validator validator;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void shouldValidateFilmWithoutId() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 2, 2));
        film.setDuration(60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
        System.out.println(violations);

    }

    @Test
    public void shouldValidateUserWithoutId() {
        User user = new User();
        user.setEmail("asd@yandex.ru");
        user.setName("Ivan");
        user.setLogin("123erw");
        user.setBirthday(LocalDate.of(2002, 2, 5));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldValidateUserWithoutName() {
        User user = new User();
        user.setEmail("asd@yandex.ru");
        user.setLogin("123erw");
        user.setBirthday(LocalDate.of(2002, 2, 5));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());

    }

    @Test
    public void shouldNotValidateFilmWithFailedDate() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("descriprion");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.size() == 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Дата должна быть не раньше 1895-12-28")));
    }

    @Test
    public void shouldValidateFilmWithDate() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("descriprion");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());

    }

    @Test
    public void shouldNotValidateFilmWithFailedName() {
        Film film = new Film();
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 5, 23));
        film.setDuration(60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.size() == 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Название должно быть указано")));
    }

    @Test
    public void shouldNotValidateFilmWithFailedDescription() {
        int length = 201;
        char repeatChar = 'a';
        String description = String.valueOf(repeatChar).repeat(length);
        Film film = new Film();
        film.setName("name");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(2000, 5, 23));
        film.setDuration(60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.size() == 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Максимальная длинна описания 200 символов")));

    }

    @Test
    public void shouldValidateFilmWithDescription() {
        int length = 200;
        char repeatChar = 'a';
        String description = String.valueOf(repeatChar).repeat(length);
        Film film = new Film();
        film.setName("name");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(2000, 5, 23));
        film.setDuration(60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotValidateUserWithFailedLoginWithSpace() {
        User user = new User();
        user.setName("name");
        user.setLogin("name name");
        user.setEmail("asd@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Логин не должно содержать пробелов")));

    }

    @Test
    public void shouldNotValidateUserWithFailedLogin() {
        User user = new User();
        user.setName("name");
        user.setEmail("asd@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.size() == 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Логин не может быть пустым")));
    }

    @Test
    public void shouldNotValidateUserWithFailedEmail() {
        User user = new User();
        user.setName("name");
        user.setLogin("name");
        user.setEmail("asdyandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.size() == 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Некорректный формат email")));
    }

    @Test
    public void shouldNotValidateUserWithEmailNull() {
        User user = new User();
        user.setName("name");
        user.setLogin("name");
        user.setBirthday(LocalDate.of(2000, 12, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.size() == 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Имейл не должен быть пустым")));
    }

    @Test
    public void shouldNotValidateUserWithBirthdayInFuture() {
        User user = new User();
        user.setName("name");
        user.setLogin("name");
        user.setEmail("asd@yandex.ru");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.size() == 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Дата рождения не может быть в будущем")));

    }


}