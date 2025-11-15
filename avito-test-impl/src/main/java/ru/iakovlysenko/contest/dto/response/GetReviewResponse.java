package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

/**
 * ДТО ответа с информацией о пулл реквестах, где пользователь назначен ревьювером.
 *
 * @author Iakov Lysenko
 */
@Builder
public record GetReviewResponse(
        @JsonProperty("user_id")
        String userId,

        @JsonProperty("pull_requests")
        List<PullRequestShortResponse> pullRequests
) {
}
