package ru.iakovlysenko.contest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iakovlysenko.contest.dto.request.CreateUserRequest;
import ru.iakovlysenko.contest.entity.PullRequest;
import ru.iakovlysenko.contest.entity.Team;
import ru.iakovlysenko.contest.entity.User;
import ru.iakovlysenko.contest.repository.PullRequestRepository;
import ru.iakovlysenko.contest.repository.TeamRepository;
import ru.iakovlysenko.contest.repository.UserRepository;
import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.response.GetReviewResponse;
import ru.iakovlysenko.contest.dto.response.UserResponse;
import ru.iakovlysenko.contest.exception.NotFoundException;
import ru.iakovlysenko.contest.mapper.PullRequestMapper;
import ru.iakovlysenko.contest.mapper.UserMapper;
import ru.iakovlysenko.contest.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса {@link UserService}.
 *
 * @author Iakov Lysenko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PullRequestRepository pullRequestRepository;
    private final TeamRepository teamRepository;
    private final UserMapper userMapper;
    private final PullRequestMapper pullRequestMapper;

    @Override
    @Transactional
    public UserResponse setIsActive(SetIsActiveRequest request) {
        log.info("Установка флага активности для пользователя {} в {}", request.userId(), request.isActive());

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + request.userId()));

        user.setIsActive(request.isActive());
        user = userRepository.save(user);

        log.info("Флаг активности пользователя {} установлен в {}", request.userId(), request.isActive());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public GetReviewResponse getReview(String userId) {
        log.info("Получение ревью для пользователя: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }

        List<PullRequest> pullRequests = pullRequestRepository.findByReviewerIdWithDetails(userId);

        GetReviewResponse response = new GetReviewResponse(
                userId,
                pullRequests.stream()
                        .map(pullRequestMapper::toShortResponse)
                        .collect(Collectors.toList())
        );

        log.info("Найдено {} PR для ревьювера {}", pullRequests.size(), userId);
        return response;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        log.info("Создание нового пользователя: {}", createUserRequest.id());

        Team team = teamRepository.findByTeamName(createUserRequest.teamName())
                .orElseThrow(() -> new NotFoundException("Команда не найдена: " + createUserRequest.teamName()));

        User newUser = User.builder()
                .id(createUserRequest.id())
                .username(createUserRequest.username())
                .team(team)
                .isActive(createUserRequest.isActive())
                .build();

        newUser = userRepository.save(newUser);

        log.info("Пользователь успешно создан: {}", createUserRequest.id());
        return userMapper.toResponse(newUser);
    }

}
