package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * ДТО ответа-обертки для пользователя.
 *
 * @author Iakov Lysenko
 */
@Builder
public record UserWrapperResponse(
        @JsonProperty("user")
        UserResponse user
) {
}
