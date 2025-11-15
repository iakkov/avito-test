package ru.iakovlysenko.contest.repository.projection;

/**
 * Проекция для количества ревьюверов по пулл реквесту.
 *
 * @author Iakov Lysenko
 */
public interface PullRequestReviewersCountProjection {

    /**
     * Идентификатор пулл реквеста.
     *
     * @return идентификатор пулл реквеста
     */
    String getPullRequestId();

    /**
     * Количество ревьюверов, назначенных на пулл реквест.
     *
     * @return количество ревьюверов
     */
    Long getReviewersCount();
}

