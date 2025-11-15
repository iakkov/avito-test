package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * ДТО ответа-обертки для команды.
 *
 * @author Iakov Lysenko
 */
@Builder
public record TeamWrapperResponse(
        @JsonProperty("team")
        TeamResponse team
) {
}
