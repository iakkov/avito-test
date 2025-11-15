package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

/**
 * ДТО ответа со статистикой назначений ревьюверов.
 *
 * @author Iakov Lysenko
 */
@Builder
public record StatisticsResponse(
        @JsonProperty("assignments_by_user")
        List<UserAssignmentsStatistic> assignmentsByUser,

        @JsonProperty("reviewers_per_pr")
        List<PullRequestReviewersStatistic> reviewersPerPullRequest
) {

    /**
     * ДТО статистики назначений для пользователя.
     */
    public record UserAssignmentsStatistic(
            @JsonProperty("user_id")
            String userId,

            @JsonProperty("assignments_count")
            Long assignmentsCount
    ) {
    }

    /**
     * ДТО статистики количества ревьюверов по пулл реквесту.
     */
    public record PullRequestReviewersStatistic(
            @JsonProperty("pull_request_id")
            String pullRequestId,

            @JsonProperty("reviewers_count")
            Long reviewersCount
    ) {
    }
}
