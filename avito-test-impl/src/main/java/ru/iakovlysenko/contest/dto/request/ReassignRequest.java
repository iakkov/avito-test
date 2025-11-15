package ru.iakovlysenko.contest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * ДТО переназначения ревьювера пулл реквеста.
 *
 * @author Iakov Lysenko
 */
public record ReassignRequest(
        @JsonProperty("pull_request_id")
        @NotBlank
        String pullRequestId,

        @JsonProperty("old_user_id")
        @NotBlank
        String oldUserId
) {
}
