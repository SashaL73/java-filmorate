package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validate.MinimumDate;

import java.time.LocalDate;

@Data
public class UpdateFilmRequest {
    private long id;
    @NotEmpty(message = "Название должно быть указано")
    private String name;
    @Size(max = 200, message = "Максимальная длинна описания 200 символов")
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || name.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasDuration() {
        return !(duration == null);
    }

}
