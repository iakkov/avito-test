package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

/**
 * ДТО ответа с информацией о команде.
 *
 * @author Iakov Lysenko
 */
@Builder
public record TeamResponse(
        @JsonProperty("team_name")
        String teamName,

        List<TeamMemberResponse> members
) {
}
