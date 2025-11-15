package ru.iakovlysenko.contest.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.response.GetReviewResponse;
import ru.iakovlysenko.contest.dto.response.PullRequestShortResponse;
import ru.iakovlysenko.contest.dto.response.UserResponse;
import ru.iakovlysenko.contest.entity.PullRequest;
import ru.iakovlysenko.contest.entity.Team;
import ru.iakovlysenko.contest.entity.User;
import ru.iakovlysenko.contest.enums.PrStatus;
import ru.iakovlysenko.contest.exception.NotFoundException;
import ru.iakovlysenko.contest.mapper.PullRequestMapper;
import ru.iakovlysenko.contest.mapper.UserMapper;
import ru.iakovlysenko.contest.repository.PullRequestRepository;
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
 * Unit тесты для {@link UserServiceImpl}.
 *
 * @author Iakov Lysenko
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PullRequestRepository pullRequestRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PullRequestMapper pullRequestMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private PullRequest pullRequest;
    private SetIsActiveRequest setIsActiveRequest;

    @BeforeEach
    void setUp() {
        Team team = Team.builder()
                .teamName("TestTeam")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .members(new ArrayList<>())
                .build();

        user = User.builder()
                .id("user1")
                .username("User1")
                .isActive(true)
                .team(team)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pullRequest = PullRequest.builder()
                .id("pr1")
                .pullRequestName("PR1")
                .author(user)
                .status(PrStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .reviewers(new ArrayList<>())
                .build();

        setIsActiveRequest = new SetIsActiveRequest("user1", false);
    }

    @Test
    @DisplayName("Успешная установка флага активности")
    void setIsActive_Success() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse userResponse = new UserResponse(
                "user1",
                "User1",
                "TestTeam",
                false
        );
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.setIsActive(setIsActiveRequest);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user1");
        assertThat(result.isActive()).isFalse();
        verify(userRepository).findById("user1");
        verify(userRepository).save(user);
        assertThat(user.getIsActive()).isFalse();
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Установка флага активности для несуществующего пользователя должна выбрасывать исключение")
    void setIsActive_UserNotFound_ThrowsException() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        SetIsActiveRequest request = new SetIsActiveRequest("nonexistent", false);

        assertThatThrownBy(() -> userService.setIsActive(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("nonexistent");
        verify(userRepository).findById("nonexistent");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Успешное получение списка PR для ревьювера")
    void getReview_Success() {
        when(userRepository.existsById("user1")).thenReturn(true);
        when(pullRequestRepository.findByReviewerIdWithDetails("user1")).thenReturn(List.of(pullRequest));

        PullRequestShortResponse prResponse = new PullRequestShortResponse(
                "pr1",
                "PR1",
                "user1",
                PrStatus.OPEN
        );
        when(pullRequestMapper.toShortResponse(any(PullRequest.class))).thenReturn(prResponse);

        GetReviewResponse result = userService.getReview("user1");

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user1");
        assertThat(result.pullRequests()).hasSize(1);
        assertThat(result.pullRequests().get(0).pullRequestId()).isEqualTo("pr1");
        verify(userRepository).existsById("user1");
        verify(pullRequestRepository).findByReviewerIdWithDetails("user1");
        verify(pullRequestMapper).toShortResponse(pullRequest);
    }

    @Test
    @DisplayName("Получение списка PR для несуществующего пользователя должно выбрасывать исключение")
    void getReview_UserNotFound_ThrowsException() {
        when(userRepository.existsById("nonexistent")).thenReturn(false);

        assertThatThrownBy(() -> userService.getReview("nonexistent"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("nonexistent");
        verify(userRepository).existsById("nonexistent");
        verify(pullRequestRepository, never()).findByReviewerIdWithDetails(anyString());
    }

    @Test
    @DisplayName("Получение пустого списка PR для ревьювера")
    void getReview_EmptyList_Success() {
        when(userRepository.existsById("user1")).thenReturn(true);
        when(pullRequestRepository.findByReviewerIdWithDetails("user1")).thenReturn(List.of());

        GetReviewResponse result = userService.getReview("user1");

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user1");
        assertThat(result.pullRequests()).isEmpty();
        verify(userRepository).existsById("user1");
        verify(pullRequestRepository).findByReviewerIdWithDetails("user1");
    }
}

