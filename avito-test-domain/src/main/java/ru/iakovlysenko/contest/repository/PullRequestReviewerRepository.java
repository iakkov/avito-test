package ru.iakovlysenko.contest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.iakovlysenko.contest.entity.PullRequestReviewer;
import ru.iakovlysenko.contest.entity.PullRequestReviewerId;
import ru.iakovlysenko.contest.repository.projection.PullRequestReviewersCountProjection;
import ru.iakovlysenko.contest.repository.projection.ReviewerAssignmentCountProjection;

import java.util.List;

/**
 * Репозиторий для {@link PullRequestReviewer}
 *
 * @author Iakov Lysenko
 */
@Repository
public interface PullRequestReviewerRepository extends JpaRepository<PullRequestReviewer, PullRequestReviewerId> {

    /**
     * Проверка существования назначения ревьювера на PR
     *
     * @param pullRequestId идентификатор PR
     * @param reviewerId идентификатор ревьювера
     * @return true, если назначение существует
     */
    boolean existsByPullRequestIdAndReviewerId(String pullRequestId, String reviewerId);

    /**
     * Удаление ревьювера из PR
     *
     * @param pullRequestId идентификатор PR
     * @param reviewerId идентификатор ревьювера
     */
    @Modifying
    @Query("DELETE FROM PullRequestReviewer prr WHERE prr.pullRequestId = :pullRequestId AND prr.reviewerId = :reviewerId")
    void deleteByPullRequestIdAndReviewerId(@Param("pullRequestId") String pullRequestId, @Param("reviewerId") String reviewerId);

    /**
     * Получение количества назначений для каждого ревьювера.
     *
     * @return список проекций с идентификатором ревьювера и количеством назначений
     */
    @Query("SELECT prr.reviewerId AS reviewerId, COUNT(prr) AS assignmentsCount "
            + "FROM PullRequestReviewer prr GROUP BY prr.reviewerId ORDER BY prr.reviewerId")
    List<ReviewerAssignmentCountProjection> countAssignmentsPerReviewer();

    /**
     * Получение количества ревьюверов для каждого пулл реквеста.
     *
     * @return список проекций с идентификатором пулл реквеста и количеством ревьюверов
     */
    @Query("SELECT prr.pullRequestId AS pullRequestId, COUNT(prr) AS reviewersCount "
            + "FROM PullRequestReviewer prr GROUP BY prr.pullRequestId ORDER BY prr.pullRequestId")
    List<PullRequestReviewersCountProjection> countReviewersPerPullRequest();
}

