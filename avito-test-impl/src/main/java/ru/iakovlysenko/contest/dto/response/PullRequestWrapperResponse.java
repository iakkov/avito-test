package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ДТО ответа-обертки для пулл реквеста.
 *
 * @author Iakov Lysenko
 */
public record PullRequestWrapperResponse(
        @JsonProperty("pr")
        PullRequestResponse pr
) {
}
