package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDate;

@Data
@Slf4j
public class User {

    private Long id;
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
