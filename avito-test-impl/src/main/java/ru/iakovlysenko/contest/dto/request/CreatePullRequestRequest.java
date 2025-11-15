package ru.iakovlysenko.contest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * ДТО создания пулл реквеста.
 *
 * @author Iakov Lysenko
 */
public record CreatePullRequestRequest(
        @JsonProperty("pull_request_id")
        @NotBlank
        String pullRequestId,

        @JsonProperty("pull_request_name")
        @NotBlank
        String pullRequestName,

        @JsonProperty("author_id")
        @NotBlank
        String authorId
) {
}
