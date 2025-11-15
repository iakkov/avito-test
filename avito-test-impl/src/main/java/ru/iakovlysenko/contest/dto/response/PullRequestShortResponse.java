package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import ru.iakovlysenko.contest.enums.PrStatus;

/**
 * ДТО ответа с краткой информацией о пулл реквесте.
 *
 * @author Iakov Lysenko
 */
@Builder
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
