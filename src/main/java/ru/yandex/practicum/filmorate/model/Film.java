package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validate.MinimumDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {

    private Long id;
    @NotEmpty(message = "Название должно быть указано")
    private String name;
    @Size(max = 200, message = "Максимальная длинна описания 200 символов")
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
    private Set<Long> likesUsersId = new HashSet<>();

}
