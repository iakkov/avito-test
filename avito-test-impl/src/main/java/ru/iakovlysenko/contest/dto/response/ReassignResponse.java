package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ДТО ответа на переназначение ревьювера.
 *
 * @author Iakov Lysenko
 */
public record ReassignResponse(
        PullRequestResponse pr,

        @JsonProperty("replaced_by")
        String replacedBy
) {
}
