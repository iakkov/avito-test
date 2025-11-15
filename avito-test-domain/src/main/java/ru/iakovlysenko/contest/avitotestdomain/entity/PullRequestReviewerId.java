package ru.iakovlysenko.contest.avitotestdomain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Составной ключ для сущности {@link PullRequestReviewer}.
 *
 * @author Iakov Lysenko
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PullRequestReviewerId implements Serializable {

    /**
     * Идентификатор пулл реквеста.
     */
    @EqualsAndHashCode.Include
    private String pullRequestId;

    /**
     * Идентификатор ревьювера.
     */
    @EqualsAndHashCode.Include
    private String reviewerId;

}

