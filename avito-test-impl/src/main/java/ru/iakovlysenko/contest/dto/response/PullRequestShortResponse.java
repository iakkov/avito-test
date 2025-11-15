package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.iakovlysenko.contest.avitotestdomain.enums.PrStatus;

/**
 * ДТО ответа с краткой информацией о пулл реквесте.
 *
 * @author Iakov Lysenko
 */
public record PullRequestShortResponse(
        @JsonProperty("pull_request_id")
        String pullRequestId,

        @JsonProperty("pull_request_name")
        String pullRequestName,

        @JsonProperty("author_id")
        String authorId,

        PrStatus status
) {
}
