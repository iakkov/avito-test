package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * ДТО ответа на переназначение ревьювера.
 *
 * @author Iakov Lysenko
 */
@Builder
public record ReassignResponse(
        PullRequestResponse pr,

        @JsonProperty("replaced_by")
        String replacedBy
) {
}
