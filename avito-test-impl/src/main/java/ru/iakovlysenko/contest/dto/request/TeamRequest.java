package ru.iakovlysenko.contest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * ДТО создания команды с участниками.
 *
 * @author Iakov Lysenko
 */
public record TeamRequest(
        @JsonProperty("team_name")
        @NotBlank
        String teamName,

        @NotEmpty
        @Valid
        List<TeamMemberRequest> members
) {
    /**
     * ДТО участника команды.
     *
     * @author Iakov Lysenko
     */
    public record TeamMemberRequest(
            @JsonProperty("user_id")
            @NotBlank
            String userId,

            @NotBlank
            String username,

            @JsonProperty("is_active")
            @NotNull
            Boolean isActive
    ) {
    }
}
