package ru.iakovlysenko.contest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iakovlysenko.contest.avitotestdomain.entity.PullRequest;
import ru.iakovlysenko.contest.avitotestdomain.entity.User;
import ru.iakovlysenko.contest.avitotestdomain.repository.PullRequestRepository;
import ru.iakovlysenko.contest.avitotestdomain.repository.UserRepository;
import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.response.GetReviewResponse;
import ru.iakovlysenko.contest.dto.response.UserResponse;
import ru.iakovlysenko.contest.exception.NotFoundException;
import ru.iakovlysenko.contest.mapper.PullRequestMapper;
import ru.iakovlysenko.contest.mapper.UserMapper;
import ru.iakovlysenko.contest.service.UserService;

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
    private final UserMapper userMapper;
    private final PullRequestMapper pullRequestMapper;
    
    @Override
    @Transactional
    public UserResponse setIsActive(SetIsActiveRequest request) {
        log.info("Setting isActive for user {} to {}", request.userId(), request.isActive());
        
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.userId()));
        
        user.setIsActive(request.isActive());
        user = userRepository.save(user);
        
        log.info("User {} isActive set to {}", request.userId(), request.isActive());
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public GetReviewResponse getReview(String userId) {
        log.info("Getting review for user: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        
        List<PullRequest> pullRequests = pullRequestRepository.findByReviewerIdWithDetails(userId);
        
        GetReviewResponse response = new GetReviewResponse(
                userId,
                pullRequests.stream()
                        .map(pullRequestMapper::toShortResponse)
                        .collect(Collectors.toList())
        );
        
        log.info("Found {} PRs for reviewer {}", pullRequests.size(), userId);
        return response;
    }

}
