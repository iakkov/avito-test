package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ДТО ответа-обертки для команды.
 *
 * @author Iakov Lysenko
 */
public record TeamWrapperResponse(
        @JsonProperty("team")
        TeamResponse team
) {
}
