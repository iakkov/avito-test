package ru.iakovlysenko.contest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ДТО запроса на создание пользователя.
 * Используется для передачи данных при создании нового пользователя через REST API.
 *
 * @author Iakov Lysenko
 */
public record CreateUserRequest(
        @NotBlank
        String id,
        @NotBlank
        String username,
        @NotBlank
        String teamName,
        @NotNull
        Boolean isActive
) {
}
