package ru.iakovlysenko.contest.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.iakovlysenko.contest.entity.PullRequest;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для {@link PullRequest}
 *
 * @author Iakov Lysenko
 */
@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, String> {

    /**
     * Поиск PR по ID
     *
     * @param id идентификатор PR
     * @return PR, если найден
     */
    Optional<PullRequest> findById(String id);

    /**
     * Поиск PR по ID с загрузкой ревьюверов
     *
     * @param id идентификатор PR
     * @return PR с загруженными ревьюверами
     */
    @EntityGraph(attributePaths = {"reviewers", "author"})
    @Query("SELECT pr FROM PullRequest pr WHERE pr.id = :id")
    Optional<PullRequest> findByIdWithReviewers(@Param("id") String id);

    /**
     * Проверка существования PR по ID
     *
     * @param id идентификатор PR
     * @return true, если PR существует
     */
    boolean existsById(String id);

    /**
     * Поиск всех PR, где указанный пользователь назначен ревьювером, с загрузкой связей.
     * Используется для эндпоинта /users/getReview
     *
     * @param reviewerId идентификатор ревьювера
     * @return список PR с загруженными связями
     */
    @EntityGraph(attributePaths = {"author", "reviewers"})
    @Query("SELECT DISTINCT pr FROM PullRequest pr JOIN pr.reviewers r WHERE r.reviewerId = :reviewerId")
    List<PullRequest> findByReviewerIdWithDetails(@Param("reviewerId") String reviewerId);
}

