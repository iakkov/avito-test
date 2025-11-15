package ru.iakovlysenko.contest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iakovlysenko.contest.entity.Team;
import ru.iakovlysenko.contest.entity.User;
import ru.iakovlysenko.contest.repository.TeamRepository;
import ru.iakovlysenko.contest.repository.UserRepository;
import ru.iakovlysenko.contest.dto.request.TeamRequest;
import ru.iakovlysenko.contest.dto.response.TeamResponse;
import ru.iakovlysenko.contest.exception.NotFoundException;
import ru.iakovlysenko.contest.exception.TeamExistsException;
import ru.iakovlysenko.contest.mapper.TeamMapper;
import ru.iakovlysenko.contest.service.TeamService;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса {@link TeamService}.
 *
 * @author Iakov Lysenko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMapper teamMapper;
    
    @Override
    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        log.info("Создание команды: {}", request.teamName());
        
        if (teamRepository.existsByTeamName(request.teamName())) {
            throw new TeamExistsException(request.teamName());
        }
        
        Team team = Team.builder()
                .teamName(request.teamName())
                .build();
        
        team = teamRepository.save(team);
        
        final Team finalTeam = team;
        
        List<User> members = new ArrayList<>();
        for (TeamRequest.TeamMemberRequest memberRequest : request.members()) {
            final String userId = memberRequest.userId();
            final String username = memberRequest.username();
            final Boolean isActive = memberRequest.isActive();
            
            User user = userRepository.findById(userId)
                    .map(existingUser -> {
                        existingUser.setUsername(username);
                        existingUser.setIsActive(isActive);
                        existingUser.setTeam(finalTeam);
                        return existingUser;
                    })
                    .orElseGet(() -> User.builder()
                            .id(userId)
                            .username(username)
                            .isActive(isActive)
                            .team(finalTeam)
                            .build());
            
            user = userRepository.save(user);
            members.add(user);
        }
        
        team.setMembers(members);
        
        log.info("Команда успешно создана: {}", request.teamName());
        return teamMapper.toResponse(team);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeam(String teamName) {
        log.info("Получение команды: {}", teamName);
        
        Team team = teamRepository.findByTeamName(teamName)
                .orElseThrow(() -> new NotFoundException("Команда не найдена: " + teamName));
        
        List<User> members = userRepository.findByTeamName(teamName);
        team.setMembers(members);
        
        return teamMapper.toResponse(team);
    }

}
