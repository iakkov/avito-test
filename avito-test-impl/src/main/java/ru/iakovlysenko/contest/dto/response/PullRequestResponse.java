package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import ru.iakovlysenko.contest.enums.PrStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ДТО ответа с информацией о пулл реквесте.
 *
 * @author Iakov Lysenko
 */
@Builder
public record PullRequestResponse(
        @JsonProperty("pull_request_id")
        String pullRequestId,

        @JsonProperty("pull_request_name")
        String pullRequestName,

        @JsonProperty("author_id")
        String authorId,

        PrStatus status,

        @JsonProperty("assigned_reviewers")
        List<String> assignedReviewers,

        @JsonProperty("createdAt")
        LocalDateTime createdAt,

        @JsonProperty("mergedAt")
        LocalDateTime mergedAt
) {
}
