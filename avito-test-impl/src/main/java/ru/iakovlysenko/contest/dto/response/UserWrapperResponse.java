package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ДТО ответа-обертки для пользователя.
 *
 * @author Iakov Lysenko
 */
public record UserWrapperResponse(
        @JsonProperty("user")
        UserResponse user
) {
}
