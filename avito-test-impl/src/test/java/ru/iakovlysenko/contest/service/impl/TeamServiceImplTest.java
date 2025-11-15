package ru.iakovlysenko.contest.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.iakovlysenko.contest.dto.request.TeamRequest;
import ru.iakovlysenko.contest.dto.response.TeamMemberResponse;
import ru.iakovlysenko.contest.dto.response.TeamResponse;
import ru.iakovlysenko.contest.entity.Team;
import ru.iakovlysenko.contest.entity.User;
import ru.iakovlysenko.contest.exception.NotFoundException;
import ru.iakovlysenko.contest.exception.TeamExistsException;
import ru.iakovlysenko.contest.mapper.TeamMapper;
import ru.iakovlysenko.contest.repository.TeamRepository;
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
 * Unit тесты для {@link TeamServiceImpl}.
 *
 * @author Iakov Lysenko
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для TeamServiceImpl")
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Team team;
    private User user1;
    private User user2;
    private TeamRequest teamRequest;

    @BeforeEach
    void setUp() {
        team = Team.builder()
                .teamName("TestTeam")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .members(new ArrayList<>())
                .build();

        user1 = User.builder()
                .id("user1")
                .username("User1")
                .isActive(true)
                .team(team)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user2 = User.builder()
                .id("user2")
                .username("User2")
                .isActive(true)
                .team(team)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        teamRequest = new TeamRequest(
                "TestTeam",
                List.of(
                        new TeamRequest.TeamMemberRequest("user1", "User1", true),
                        new TeamRequest.TeamMemberRequest("user2", "User2", true)
                )
        );
    }

    @Test
    @DisplayName("Успешное создание команды")
    void createTeam_Success() {
        when(teamRepository.existsByTeamName("TestTeam")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(userRepository.findById("user1")).thenReturn(Optional.empty());
        when(userRepository.findById("user2")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user1, user2);

        TeamResponse teamResponse = new TeamResponse(
                "TestTeam",
                List.of(
                        new TeamMemberResponse("user1", "User1", true),
                        new TeamMemberResponse("user2", "User2", true)
                )
        );
        when(teamMapper.toResponse(any(Team.class))).thenReturn(teamResponse);

        TeamResponse result = teamService.createTeam(teamRequest);

        assertThat(result).isNotNull();
        assertThat(result.teamName()).isEqualTo("TestTeam");
        assertThat(result.members()).hasSize(2);
        verify(teamRepository).existsByTeamName("TestTeam");
        verify(teamRepository).save(any(Team.class));
        verify(userRepository, times(2)).findById(anyString());
        verify(userRepository, times(2)).save(any(User.class));
        verify(teamMapper).toResponse(any(Team.class));
    }

    @Test
    @DisplayName("Создание команды с существующим именем должно выбрасывать исключение")
    void createTeam_TeamExists_ThrowsException() {
        when(teamRepository.existsByTeamName("TestTeam")).thenReturn(true);

        assertThatThrownBy(() -> teamService.createTeam(teamRequest))
                .isInstanceOf(TeamExistsException.class)
                .hasMessageContaining("TestTeam");
        verify(teamRepository).existsByTeamName("TestTeam");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Создание команды с обновлением существующих пользователей")
    void createTeam_UpdateExistingUsers_Success() {
        User existingUser = User.builder()
                .id("user1")
                .username("OldUsername")
                .isActive(false)
                .team(null)
                .build();

        when(teamRepository.existsByTeamName("TestTeam")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(userRepository.findById("user1")).thenReturn(Optional.of(existingUser));
        when(userRepository.findById("user2")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId().equals("user1")) {
                return user1;
            } else {
                return user2;
            }
        });

        TeamResponse teamResponse = new TeamResponse(
                "TestTeam",
                List.of(
                        new TeamMemberResponse("user1", "User1", true),
                        new TeamMemberResponse("user2", "User2", true)
                )
        );
        when(teamMapper.toResponse(any(Team.class))).thenReturn(teamResponse);

        TeamResponse result = teamService.createTeam(teamRequest);

        assertThat(result).isNotNull();
        verify(userRepository).findById("user1");
        verify(userRepository, atLeastOnce()).save(any(User.class));
        assertThat(existingUser.getUsername()).isEqualTo("User1");
        assertThat(existingUser.getIsActive()).isTrue();
        assertThat(existingUser.getTeam()).isEqualTo(team);
    }

    @Test
    @DisplayName("Успешное получение команды")
    void getTeam_Success() {
        team.setMembers(List.of(user1, user2));
        when(teamRepository.findByTeamName("TestTeam")).thenReturn(Optional.of(team));
        when(userRepository.findByTeamName("TestTeam")).thenReturn(List.of(user1, user2));

        TeamResponse teamResponse = new TeamResponse(
                "TestTeam",
                List.of(
                        new TeamMemberResponse("user1", "User1", true),
                        new TeamMemberResponse("user2", "User2", true)
                )
        );
        when(teamMapper.toResponse(team)).thenReturn(teamResponse);

        TeamResponse result = teamService.getTeam("TestTeam");

        assertThat(result).isNotNull();
        assertThat(result.teamName()).isEqualTo("TestTeam");
        verify(teamRepository).findByTeamName("TestTeam");
        verify(userRepository).findByTeamName("TestTeam");
        verify(teamMapper).toResponse(team);
    }

    @Test
    @DisplayName("Получение несуществующей команды должно выбрасывать исключение")
    void getTeam_NotFound_ThrowsException() {
        when(teamRepository.findByTeamName("NonExistentTeam")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.getTeam("NonExistentTeam"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("NonExistentTeam");
        verify(teamRepository).findByTeamName("NonExistentTeam");
        verify(userRepository, never()).findByTeamName(anyString());
    }
}

