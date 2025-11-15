package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * ДТО ответа с информацией об участнике команды.
 *
 * @author Iakov Lysenko
 */
@Builder
public record TeamMemberResponse(
        @JsonProperty("user_id")
        String userId,

        String username,

        @JsonProperty("is_active")
        Boolean isActive
) {
}
