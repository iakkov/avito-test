package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ДТО ответа с информацией о пользователе.
 *
 * @author Iakov Lysenko
 */
public record UserResponse(
        @JsonProperty("user_id")
        String userId,

        String username,

        @JsonProperty("team_name")
        String teamName,

        @JsonProperty("is_active")
        Boolean isActive
) {
}
