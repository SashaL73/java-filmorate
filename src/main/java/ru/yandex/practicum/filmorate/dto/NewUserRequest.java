package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @Email(message = "Некорректный формат email")
    @NotEmpty(message = "Имейл не должен быть пустым")
    private String email;
    @NotEmpty(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должно содержать пробелов")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
