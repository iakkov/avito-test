package ru.iakovlysenko.contest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * ДТО слияния пулл реквеста.
 *
 * @author Iakov Lysenko
 */
public record MergePullRequestRequest(
        @JsonProperty("pull_request_id")
        @NotBlank
        String pullRequestId
) {
}
