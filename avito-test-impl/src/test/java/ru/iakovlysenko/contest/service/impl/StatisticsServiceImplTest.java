package ru.iakovlysenko.contest.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.iakovlysenko.contest.dto.response.StatisticsResponse;
import ru.iakovlysenko.contest.projection.PullRequestReviewersCountProjection;
import ru.iakovlysenko.contest.projection.ReviewerAssignmentCountProjection;
import ru.iakovlysenko.contest.repository.PullRequestReviewerRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit тесты для {@link StatisticsServiceImpl}.
 *
 * @author Iakov Lysenko
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для StatisticsServiceImpl")
class StatisticsServiceImplTest {

    @Mock
    private PullRequestReviewerRepository pullRequestReviewerRepository;

    @Mock
    private ReviewerAssignmentCountProjection assignmentProjection1;

    @Mock
    private ReviewerAssignmentCountProjection assignmentProjection2;

    @Mock
    private PullRequestReviewersCountProjection reviewersProjection1;

    @Mock
    private PullRequestReviewersCountProjection reviewersProjection2;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    @DisplayName("Успешное получение статистики с данными")
    void getStatistics_Success() {
        when(assignmentProjection1.getReviewerId()).thenReturn("user1");
        when(assignmentProjection1.getAssignmentsCount()).thenReturn(5L);
        when(assignmentProjection2.getReviewerId()).thenReturn("user2");
        when(assignmentProjection2.getAssignmentsCount()).thenReturn(3L);
        when(reviewersProjection1.getPullRequestId()).thenReturn("pr1");
        when(reviewersProjection1.getReviewersCount()).thenReturn(2L);
        when(reviewersProjection2.getPullRequestId()).thenReturn("pr2");
        when(reviewersProjection2.getReviewersCount()).thenReturn(1L);

        when(pullRequestReviewerRepository.countAssignmentsPerReviewer())
                .thenReturn(List.of(assignmentProjection1, assignmentProjection2));

        when(pullRequestReviewerRepository.countReviewersPerPullRequest())
                .thenReturn(List.of(reviewersProjection1, reviewersProjection2));

        StatisticsResponse result = statisticsService.getStatistics();

        assertThat(result).isNotNull();
        assertThat(result.assignmentsByUser()).hasSize(2);
        assertThat(result.reviewersPerPullRequest()).hasSize(2);

        StatisticsResponse.UserAssignmentsStatistic assignment1 = result.assignmentsByUser().get(0);
        assertThat(assignment1.userId()).isEqualTo("user1");
        assertThat(assignment1.assignmentsCount()).isEqualTo(5L);

        StatisticsResponse.UserAssignmentsStatistic assignment2 = result.assignmentsByUser().get(1);
        assertThat(assignment2.userId()).isEqualTo("user2");
        assertThat(assignment2.assignmentsCount()).isEqualTo(3L);

        StatisticsResponse.PullRequestReviewersStatistic reviewers1 = result.reviewersPerPullRequest().get(0);
        assertThat(reviewers1.pullRequestId()).isEqualTo("pr1");
        assertThat(reviewers1.reviewersCount()).isEqualTo(2L);

        StatisticsResponse.PullRequestReviewersStatistic reviewers2 = result.reviewersPerPullRequest().get(1);
        assertThat(reviewers2.pullRequestId()).isEqualTo("pr2");
        assertThat(reviewers2.reviewersCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Получение статистики с пустыми данными")
    void getStatistics_EmptyData_Success() {
        when(pullRequestReviewerRepository.countAssignmentsPerReviewer())
                .thenReturn(List.of());

        when(pullRequestReviewerRepository.countReviewersPerPullRequest())
                .thenReturn(List.of());

        StatisticsResponse result = statisticsService.getStatistics();

        assertThat(result).isNotNull();
        assertThat(result.assignmentsByUser()).isEmpty();
        assertThat(result.reviewersPerPullRequest()).isEmpty();
    }

    @Test
    @DisplayName("Получение статистики с одним назначением по пользователю")
    void getStatistics_SingleAssignment_Success() {
        when(assignmentProjection1.getReviewerId()).thenReturn("user1");
        when(assignmentProjection1.getAssignmentsCount()).thenReturn(5L);

        when(pullRequestReviewerRepository.countAssignmentsPerReviewer())
                .thenReturn(List.of(assignmentProjection1));

        when(pullRequestReviewerRepository.countReviewersPerPullRequest())
                .thenReturn(List.of());

        StatisticsResponse result = statisticsService.getStatistics();

        assertThat(result).isNotNull();
        assertThat(result.assignmentsByUser()).hasSize(1);
        assertThat(result.reviewersPerPullRequest()).isEmpty();

        StatisticsResponse.UserAssignmentsStatistic assignment = result.assignmentsByUser().get(0);
        assertThat(assignment.userId()).isEqualTo("user1");
        assertThat(assignment.assignmentsCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Получение статистики с одним количеством ревьюверов по PR")
    void getStatistics_SingleReviewersCount_Success() {
        when(reviewersProjection1.getPullRequestId()).thenReturn("pr1");
        when(reviewersProjection1.getReviewersCount()).thenReturn(2L);

        when(pullRequestReviewerRepository.countAssignmentsPerReviewer())
                .thenReturn(List.of());

        when(pullRequestReviewerRepository.countReviewersPerPullRequest())
                .thenReturn(List.of(reviewersProjection1));

        StatisticsResponse result = statisticsService.getStatistics();

        assertThat(result).isNotNull();
        assertThat(result.assignmentsByUser()).isEmpty();
        assertThat(result.reviewersPerPullRequest()).hasSize(1);

        StatisticsResponse.PullRequestReviewersStatistic reviewers = result.reviewersPerPullRequest().get(0);
        assertThat(reviewers.pullRequestId()).isEqualTo("pr1");
        assertThat(reviewers.reviewersCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Получение статистики с большим количеством данных")
    void getStatistics_LargeDataSet_Success() {
        when(assignmentProjection1.getReviewerId()).thenReturn("user1");
        when(assignmentProjection1.getAssignmentsCount()).thenReturn(5L);
        when(assignmentProjection2.getReviewerId()).thenReturn("user2");
        when(assignmentProjection2.getAssignmentsCount()).thenReturn(3L);
        when(reviewersProjection1.getPullRequestId()).thenReturn("pr1");
        when(reviewersProjection1.getReviewersCount()).thenReturn(2L);
        when(reviewersProjection2.getPullRequestId()).thenReturn("pr2");
        when(reviewersProjection2.getReviewersCount()).thenReturn(1L);

        List<ReviewerAssignmentCountProjection> manyAssignments = List.of(
                assignmentProjection1, assignmentProjection2, assignmentProjection1, assignmentProjection2
        );

        List<PullRequestReviewersCountProjection> manyReviewers = List.of(
                reviewersProjection1, reviewersProjection2, reviewersProjection1
        );

        when(pullRequestReviewerRepository.countAssignmentsPerReviewer())
                .thenReturn(manyAssignments);

        when(pullRequestReviewerRepository.countReviewersPerPullRequest())
                .thenReturn(manyReviewers);

        StatisticsResponse result = statisticsService.getStatistics();

        assertThat(result).isNotNull();
        assertThat(result.assignmentsByUser()).hasSize(4);
        assertThat(result.reviewersPerPullRequest()).hasSize(3);
    }

}
