package ru.iakovlysenko.contest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ДТО установки флага активности пользователя.
 *
 * @author Iakov Lysenko
 */
public record SetIsActiveRequest(
        @JsonProperty("user_id")
        @NotBlank
        String userId,

        @JsonProperty("is_active")
        @NotNull
        Boolean isActive
) {
}
