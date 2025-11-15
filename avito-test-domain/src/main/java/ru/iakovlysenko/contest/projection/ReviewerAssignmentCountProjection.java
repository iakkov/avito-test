package ru.iakovlysenko.contest.projection;

/**
 * Проекция для количества назначений ревьюверов.
 *
 * @author Iakov Lysenko
 */
public interface ReviewerAssignmentCountProjection {

    /**
     * Идентификатор ревьювера.
     *
     * @return идентификатор ревьювера
     */
    String getReviewerId();

    /**
     * Количество назначений ревьювера.
     *
     * @return количество назначений
     */
    Long getAssignmentsCount();
}
