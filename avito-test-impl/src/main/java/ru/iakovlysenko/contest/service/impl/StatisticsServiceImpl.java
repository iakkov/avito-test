package ru.iakovlysenko.contest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iakovlysenko.contest.dto.response.StatisticsResponse;
import ru.iakovlysenko.contest.repository.PullRequestReviewerRepository;
import ru.iakovlysenko.contest.service.StatisticsService;

import java.util.List;

/**
 * Реализация сервиса {@link StatisticsService}.
 *
 * @author Iakov Lysenko
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final PullRequestReviewerRepository pullRequestReviewerRepository;

    @Override
    @Transactional(readOnly = true)
    public StatisticsResponse getStatistics() {
        log.info("Получение статистики назначений ревьюверов");

        List<StatisticsResponse.UserAssignmentsStatistic> assignmentsByUser = pullRequestReviewerRepository
                .countAssignmentsPerReviewer()
                .stream()
                .map(projection -> new StatisticsResponse.UserAssignmentsStatistic(
                        projection.getReviewerId(),
                        projection.getAssignmentsCount()
                ))
                .toList();

        List<StatisticsResponse.PullRequestReviewersStatistic> reviewersPerPullRequest = pullRequestReviewerRepository
                .countReviewersPerPullRequest()
                .stream()
                .map(projection -> new StatisticsResponse.PullRequestReviewersStatistic(
                        projection.getPullRequestId(),
                        projection.getReviewersCount()
                ))
                .toList();

        return new StatisticsResponse(assignmentsByUser, reviewersPerPullRequest);
    }
}
