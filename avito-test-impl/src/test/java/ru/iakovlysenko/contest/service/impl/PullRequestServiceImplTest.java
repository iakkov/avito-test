package ru.iakovlysenko.contest.service.impl;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.iakovlysenko.contest.dto.request.CreatePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.MergePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.ReassignRequest;
import ru.iakovlysenko.contest.dto.response.PullRequestResponse;
import ru.iakovlysenko.contest.dto.response.ReassignResponse;
import ru.iakovlysenko.contest.entity.PullRequest;
import ru.iakovlysenko.contest.entity.PullRequestReviewer;
import ru.iakovlysenko.contest.entity.Team;
import ru.iakovlysenko.contest.entity.User;
import ru.iakovlysenko.contest.enums.PrStatus;
import ru.iakovlysenko.contest.exception.*;
import ru.iakovlysenko.contest.mapper.PullRequestMapper;
import ru.iakovlysenko.contest.repository.PullRequestRepository;
import ru.iakovlysenko.contest.repository.PullRequestReviewerRepository;
import ru.iakovlysenko.contest.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для {@link PullRequestServiceImpl}.
 *
 * @author Iakov Lysenko
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для PullRequestServiceImpl")
class PullRequestServiceImplTest {

    @Mock
    private PullRequestRepository pullRequestRepository;

    @Mock
    private PullRequestReviewerRepository pullRequestReviewerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PullRequestMapper pullRequestMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PullRequestServiceImpl pullRequestService;

    private Team team;
    private User author;
    private User reviewer1;
    private User reviewer2;
    private PullRequest pullRequest;
    private CreatePullRequestRequest createRequest;
    private MergePullRequestRequest mergeRequest;
    private ReassignRequest reassignRequest;

    @BeforeEach
    void setUp() {
        team = Team.builder()
                .teamName("TestTeam")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .members(new ArrayList<>())
                .build();

        author = User.builder()
                .id("author1")
                .username("Author1")
                .isActive(true)
                .team(team)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        reviewer1 = User.builder()
                .id("reviewer1")
                .username("Reviewer1")
                .isActive(true)
                .team(team)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        reviewer2 = User.builder()
                .id("reviewer2")
                .username("Reviewer2")
                .isActive(true)
                .team(team)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pullRequest = PullRequest.builder()
                .id("pr1")
                .pullRequestName("PR1")
                .author(author)
                .status(PrStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .reviewers(new ArrayList<>())
                .build();

        createRequest = new CreatePullRequestRequest("pr1", "PR1", "author1");
        mergeRequest = new MergePullRequestRequest("pr1");
        reassignRequest = new ReassignRequest("pr1", "reviewer1");
    }

    @Test
    @DisplayName("Успешное создание PR с назначением ревьюверов")
    void createPullRequest_Success() {
        when(pullRequestRepository.existsById("pr1")).thenReturn(false);
        when(userRepository.findById("author1")).thenReturn(Optional.of(author));
        when(pullRequestRepository.save(any(PullRequest.class))).thenReturn(pullRequest);
        when(userRepository.findActiveUsersByTeamExcludingUser(team, "author1"))
                .thenReturn(new ArrayList<>(List.of(reviewer1, reviewer2)));
        when(pullRequestReviewerRepository.save(any(PullRequestReviewer.class)))
                .thenReturn(new PullRequestReviewer());
        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));

        PullRequestResponse prResponse = new PullRequestResponse(
                "pr1",
                "PR1",
                "author1",
                PrStatus.OPEN,
                List.of("reviewer1", "reviewer2"),
                LocalDateTime.now(),
                null
        );
        when(pullRequestMapper.toResponse(any(PullRequest.class))).thenReturn(prResponse);

        PullRequestResponse result = pullRequestService.createPullRequest(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.pullRequestId()).isEqualTo("pr1");
        assertThat(result.assignedReviewers()).hasSize(2);
        verify(pullRequestRepository).existsById("pr1");
        verify(userRepository).findById("author1");
        verify(pullRequestRepository).save(any(PullRequest.class));
        verify(userRepository).findActiveUsersByTeamExcludingUser(team, "author1");
        verify(pullRequestReviewerRepository, atMost(2)).save(any(PullRequestReviewer.class));
        verify(entityManager).flush();
        verify(entityManager).clear();
        verify(pullRequestRepository).findByIdWithReviewers("pr1");
        verify(pullRequestMapper).toResponse(pullRequest);
    }

    @Test
    @DisplayName("Создание PR с существующим ID должно выбрасывать исключение")
    void createPullRequest_PrExists_ThrowsException() {
        when(pullRequestRepository.existsById("pr1")).thenReturn(true);

        assertThatThrownBy(() -> pullRequestService.createPullRequest(createRequest))
                .isInstanceOf(PrExistsException.class);
        verify(pullRequestRepository).existsById("pr1");
        verify(pullRequestRepository, never()).save(any(PullRequest.class));
    }

    @Test
    @DisplayName("Создание PR с несуществующим автором должно выбрасывать исключение")
    void createPullRequest_AuthorNotFound_ThrowsException() {
        when(pullRequestRepository.existsById("pr1")).thenReturn(false);
        when(userRepository.findById("author1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pullRequestService.createPullRequest(createRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Автор не найден");
        verify(pullRequestRepository).existsById("pr1");
        verify(userRepository).findById("author1");
        verify(pullRequestRepository, never()).save(any(PullRequest.class));
    }

    @Test
    @DisplayName("Создание PR с неактивным автором должно выбрасывать исключение")
    void createPullRequest_InactiveAuthor_ThrowsException() {
        author.setIsActive(false);
        when(pullRequestRepository.existsById("pr1")).thenReturn(false);
        when(userRepository.findById("author1")).thenReturn(Optional.of(author));

        assertThatThrownBy(() -> pullRequestService.createPullRequest(createRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Автор не активен");
        verify(pullRequestRepository).existsById("pr1");
        verify(userRepository).findById("author1");
        verify(pullRequestRepository, never()).save(any(PullRequest.class));
    }

    @Test
    @DisplayName("Создание PR без доступных ревьюверов")
    void createPullRequest_NoReviewers_Success() {
        when(pullRequestRepository.existsById("pr1")).thenReturn(false);
        when(userRepository.findById("author1")).thenReturn(Optional.of(author));
        when(pullRequestRepository.save(any(PullRequest.class))).thenReturn(pullRequest);
        when(userRepository.findActiveUsersByTeamExcludingUser(team, "author1"))
                .thenReturn(List.of());
        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));

        PullRequestResponse prResponse = new PullRequestResponse(
                "pr1",
                "PR1",
                "author1",
                PrStatus.OPEN,
                List.of(),
                LocalDateTime.now(),
                null
        );
        when(pullRequestMapper.toResponse(any(PullRequest.class))).thenReturn(prResponse);

        PullRequestResponse result = pullRequestService.createPullRequest(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.assignedReviewers()).isEmpty();
        verify(pullRequestReviewerRepository, never()).save(any(PullRequestReviewer.class));
    }

    @Test
    @DisplayName("Успешное слияние PR")
    void mergePullRequest_Success() {
        when(pullRequestRepository.findById("pr1")).thenReturn(Optional.of(pullRequest));
        when(pullRequestRepository.save(any(PullRequest.class))).thenReturn(pullRequest);
        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));

        PullRequestResponse prResponse = new PullRequestResponse(
                "pr1",
                "PR1",
                "author1",
                PrStatus.MERGED,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(pullRequestMapper.toResponse(any(PullRequest.class))).thenReturn(prResponse);

        PullRequestResponse result = pullRequestService.mergePullRequest(mergeRequest);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PrStatus.MERGED);
        assertThat(pullRequest.getStatus()).isEqualTo(PrStatus.MERGED);
        verify(pullRequestRepository).findById("pr1");
        verify(pullRequestRepository).save(pullRequest);
        verify(pullRequestRepository).findByIdWithReviewers("pr1");
    }

    @Test
    @DisplayName("Идемпотентность merge - повторное слияние уже слитого PR")
    void mergePullRequest_AlreadyMerged_Idempotent() {
        pullRequest.setStatus(PrStatus.MERGED);
        pullRequest.setMergedAt(LocalDateTime.now());
        when(pullRequestRepository.findById("pr1")).thenReturn(Optional.of(pullRequest));
        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));

        PullRequestResponse prResponse = new PullRequestResponse(
                "pr1",
                "PR1",
                "author1",
                PrStatus.MERGED,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(pullRequestMapper.toResponse(any(PullRequest.class))).thenReturn(prResponse);

        PullRequestResponse result = pullRequestService.mergePullRequest(mergeRequest);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PrStatus.MERGED);
        verify(pullRequestRepository).findById("pr1");
        verify(pullRequestRepository, never()).save(any(PullRequest.class));
        verify(pullRequestRepository).findByIdWithReviewers("pr1");
    }

    @Test
    @DisplayName("Слияние несуществующего PR должно выбрасывать исключение")
    void mergePullRequest_NotFound_ThrowsException() {
        when(pullRequestRepository.findById("pr1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pullRequestService.mergePullRequest(mergeRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пулл реквест не найден");
        verify(pullRequestRepository).findById("pr1");
        verify(pullRequestRepository, never()).save(any(PullRequest.class));
    }

    @Test
    @DisplayName("Успешное переназначение ревьювера")
    void reassignReviewer_Success() {
        PullRequestReviewer reviewer = PullRequestReviewer.builder()
                .pullRequestId("pr1")
                .reviewerId("reviewer1")
                .build();
        pullRequest.getReviewers().add(reviewer);

        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));
        when(pullRequestReviewerRepository.existsByPullRequestIdAndReviewerId("pr1", "reviewer1"))
                .thenReturn(true);
        when(userRepository.findById("reviewer1")).thenReturn(Optional.of(reviewer1));
        when(userRepository.findActiveTeamMembersExcludingUser("reviewer1"))
                .thenReturn(List.of(reviewer2));
        doNothing().when(pullRequestReviewerRepository).deleteByPullRequestIdAndReviewerId("pr1", "reviewer1");
        when(pullRequestReviewerRepository.save(any(PullRequestReviewer.class)))
                .thenReturn(new PullRequestReviewer());
        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));

        PullRequestResponse prResponse = new PullRequestResponse(
                "pr1",
                "PR1",
                "author1",
                PrStatus.OPEN,
                List.of("reviewer2"),
                LocalDateTime.now(),
                null
        );
        when(pullRequestMapper.toResponse(any(PullRequest.class))).thenReturn(prResponse);

        ReassignResponse result = pullRequestService.reassignReviewer(reassignRequest);

        assertThat(result).isNotNull();
        assertThat(result.replacedBy()).isEqualTo("reviewer2");
        verify(pullRequestRepository, times(2)).findByIdWithReviewers("pr1");
        verify(pullRequestReviewerRepository).existsByPullRequestIdAndReviewerId("pr1", "reviewer1");
        verify(userRepository).findById("reviewer1");
        verify(userRepository).findActiveTeamMembersExcludingUser("reviewer1");
        verify(pullRequestReviewerRepository).deleteByPullRequestIdAndReviewerId("pr1", "reviewer1");
        verify(pullRequestReviewerRepository).save(any(PullRequestReviewer.class));
    }

    @Test
    @DisplayName("Переназначение ревьювера на слитом PR должно выбрасывать исключение")
    void reassignReviewer_MergedPr_ThrowsException() {
        pullRequest.setStatus(PrStatus.MERGED);
        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));

        assertThatThrownBy(() -> pullRequestService.reassignReviewer(reassignRequest))
                .isInstanceOf(PrMergedException.class);
        verify(pullRequestRepository).findByIdWithReviewers("pr1");
        verify(pullRequestReviewerRepository, never()).deleteByPullRequestIdAndReviewerId(anyString(), anyString());
    }

    @Test
    @DisplayName("Переназначение не назначенного ревьювера должно выбрасывать исключение")
    void reassignReviewer_NotAssigned_ThrowsException() {
        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));
        when(pullRequestReviewerRepository.existsByPullRequestIdAndReviewerId("pr1", "reviewer1"))
                .thenReturn(false);

        assertThatThrownBy(() -> pullRequestService.reassignReviewer(reassignRequest))
                .isInstanceOf(NotAssignedException.class);
        verify(pullRequestRepository).findByIdWithReviewers("pr1");
        verify(pullRequestReviewerRepository).existsByPullRequestIdAndReviewerId("pr1", "reviewer1");
        verify(pullRequestReviewerRepository, never()).deleteByPullRequestIdAndReviewerId(anyString(), anyString());
    }

    @Test
    @DisplayName("Переназначение ревьювера без доступных кандидатов должно выбрасывать исключение")
    void reassignReviewer_NoCandidates_ThrowsException() {
        when(pullRequestRepository.findByIdWithReviewers("pr1")).thenReturn(Optional.of(pullRequest));
        when(pullRequestReviewerRepository.existsByPullRequestIdAndReviewerId("pr1", "reviewer1"))
                .thenReturn(true);
        when(userRepository.findById("reviewer1")).thenReturn(Optional.of(reviewer1));
        when(userRepository.findActiveTeamMembersExcludingUser("reviewer1"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> pullRequestService.reassignReviewer(reassignRequest))
                .isInstanceOf(NoCandidateException.class);
        verify(userRepository).findActiveTeamMembersExcludingUser("reviewer1");
        verify(pullRequestReviewerRepository, never()).deleteByPullRequestIdAndReviewerId(anyString(), anyString());
    }
}

