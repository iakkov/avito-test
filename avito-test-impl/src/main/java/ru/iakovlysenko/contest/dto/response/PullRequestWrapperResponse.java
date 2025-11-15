package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * ДТО ответа-обертки для пулл реквеста.
 *
 * @author Iakov Lysenko
 */
@Builder
public record PullRequestWrapperResponse(
        @JsonProperty("pr")
        PullRequestResponse pr
) {
}
