package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * ДТО ответа с информацией о пользователе.
 *
 * @author Iakov Lysenko
 */
@Builder
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
