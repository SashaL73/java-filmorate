package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validate.MinimumDate;

import java.time.LocalDate;
import java.util.Set;

@Data
public class NewFilmRequest {
    @NotEmpty(message = "Название должно быть указано")
    private String name;
    @Size(max = 200, message = "Максимальная длинна описания 200 символов")
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
    private Mpa mpa;
    private Set<Genre> genres;
}
