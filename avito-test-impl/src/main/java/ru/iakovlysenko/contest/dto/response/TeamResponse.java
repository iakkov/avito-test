package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * ДТО ответа с информацией о команде.
 *
 * @author Iakov Lysenko
 */
public record TeamResponse(
        @JsonProperty("team_name")
        String teamName,

        List<TeamMemberResponse> members
) {
}
