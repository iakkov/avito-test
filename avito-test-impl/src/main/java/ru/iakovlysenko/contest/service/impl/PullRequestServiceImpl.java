package ru.iakovlysenko.contest.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iakovlysenko.contest.entity.PullRequest;
import ru.iakovlysenko.contest.entity.PullRequestReviewer;
import ru.iakovlysenko.contest.entity.User;
import ru.iakovlysenko.contest.enums.PrStatus;
import ru.iakovlysenko.contest.repository.PullRequestRepository;
import ru.iakovlysenko.contest.repository.PullRequestReviewerRepository;
import ru.iakovlysenko.contest.repository.UserRepository;
import ru.iakovlysenko.contest.dto.request.CreatePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.MergePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.ReassignRequest;
import ru.iakovlysenko.contest.dto.response.PullRequestResponse;
import ru.iakovlysenko.contest.dto.response.ReassignResponse;
import ru.iakovlysenko.contest.exception.NoCandidateException;
import ru.iakovlysenko.contest.exception.NotAssignedException;
import ru.iakovlysenko.contest.exception.NotFoundException;
import ru.iakovlysenko.contest.exception.PrExistsException;
import ru.iakovlysenko.contest.exception.PrMergedException;
import ru.iakovlysenko.contest.mapper.PullRequestMapper;
import ru.iakovlysenko.contest.service.PullRequestService;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Реализация сервиса {@link PullRequestService}.
 *
 * @author Iakov Lysenko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PullRequestServiceImpl implements PullRequestService {
    
    private static final int MAX_REVIEWERS = 2;
    private static final Random RANDOM = new Random();
    
    private final PullRequestRepository pullRequestRepository;
    private final PullRequestReviewerRepository pullRequestReviewerRepository;
    private final UserRepository userRepository;
    private final PullRequestMapper pullRequestMapper;
    private final EntityManager entityManager;
    
    @Override
    @Transactional
    public PullRequestResponse createPullRequest(CreatePullRequestRequest request) {
        log.info("Создание пулл реквеста: {}", request.pullRequestId());
        
        if (pullRequestRepository.existsById(request.pullRequestId())) {
            throw new PrExistsException(request.pullRequestId());
        }
        
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new NotFoundException("Автор не найден: " + request.authorId()));
        
        if (!author.getIsActive()) {
            throw new NotFoundException("Автор не активен: " + request.authorId());
        }
        
        PullRequest pullRequest = PullRequest.builder()
                .id(request.pullRequestId())
                .pullRequestName(request.pullRequestName())
                .author(author)
                .status(PrStatus.OPEN)
                .build();
        
        pullRequest = pullRequestRepository.save(pullRequest);
        
        final String pullRequestId = pullRequest.getId();
        
        List<User> candidateReviewers = userRepository.findActiveUsersByTeamExcludingUser(
                author.getTeam(),
                author.getId()
        );
        
        List<User> selectedReviewers;
        if (candidateReviewers.isEmpty()) {
            selectedReviewers = Collections.emptyList();
        } else {
            Collections.shuffle(candidateReviewers);
            int reviewersCount = Math.min(candidateReviewers.size(), MAX_REVIEWERS);
            selectedReviewers = candidateReviewers.subList(0, reviewersCount);
        }
        
        for (User reviewer : selectedReviewers) {
            PullRequestReviewer reviewerAssignment = PullRequestReviewer.builder()
                    .pullRequestId(pullRequestId)
                    .reviewerId(reviewer.getId())
                    .build();
            
            pullRequestReviewerRepository.save(reviewerAssignment);
        }
        
        entityManager.flush();
        entityManager.clear();

        pullRequest = pullRequestRepository.findByIdWithReviewers(pullRequestId)
                .orElseThrow(() -> new NotFoundException("Пулл реквест не найден: " + pullRequestId));
        
        log.info("Пулл реквест успешно создан: {} с {} ревьюверами", 
                request.pullRequestId(), selectedReviewers.size());
        
        return pullRequestMapper.toResponse(pullRequest);
    }
    
    @Override
    @Transactional
    public PullRequestResponse mergePullRequest(MergePullRequestRequest request) {
        log.info("Слияние пулл реквеста: {}", request.pullRequestId());
        
        final String pullRequestId = request.pullRequestId();
        PullRequest pullRequest = pullRequestRepository.findById(pullRequestId)
                .orElseThrow(() -> new NotFoundException("Пулл реквест не найден: " + pullRequestId));
        
        if (pullRequest.getStatus() == PrStatus.MERGED) {
            log.info("Пулл реквест {} уже слит", pullRequestId);
            pullRequest = pullRequestRepository.findByIdWithReviewers(pullRequestId)
                    .orElseThrow(() -> new NotFoundException("Пулл реквест не найден: " + pullRequestId));
            return pullRequestMapper.toResponse(pullRequest);
        }
        
        pullRequest.setStatus(PrStatus.MERGED);
        pullRequest = pullRequestRepository.save(pullRequest);
        
        pullRequest = pullRequestRepository.findByIdWithReviewers(pullRequestId)
                .orElseThrow(() -> new NotFoundException("Пулл реквест не найден: " + pullRequestId));
        
        log.info("Пулл реквест успешно слит: {}", request.pullRequestId());
        return pullRequestMapper.toResponse(pullRequest);
    }
    
    @Override
    @Transactional
    public ReassignResponse reassignReviewer(ReassignRequest request) {
        log.info("Переназначение ревьювера {} для пулл реквеста {}", 
                request.oldUserId(), request.pullRequestId());
        
        final String pullRequestId = request.pullRequestId();
        final String oldUserId = request.oldUserId();
        
        PullRequest pullRequest = pullRequestRepository.findByIdWithReviewers(pullRequestId)
                .orElseThrow(() -> new NotFoundException("Пулл реквест не найден: " + pullRequestId));
        
        if (pullRequest.getStatus() == PrStatus.MERGED) {
            throw new PrMergedException(pullRequestId);
        }
        
        boolean isAssigned = pullRequestReviewerRepository.existsByPullRequestIdAndReviewerId(
                pullRequestId,
                oldUserId
        );
        
        if (!isAssigned) {
            throw new NotAssignedException(oldUserId, pullRequestId);
        }
        
        User oldReviewer = userRepository.findById(oldUserId)
                .orElseThrow(() -> new NotFoundException("Ревьювер не найден: " + oldUserId));
        
        List<User> candidateReviewers = userRepository.findActiveTeamMembersExcludingUser(oldReviewer.getId());
        
        if (candidateReviewers.isEmpty()) {
            throw new NoCandidateException("нет активного кандидата для замены в команде");
        }
        
        User newReviewer = candidateReviewers.get(RANDOM.nextInt(candidateReviewers.size()));
        
        pullRequestReviewerRepository.deleteByPullRequestIdAndReviewerId(
                pullRequestId,
                oldUserId
        );
        
        PullRequestReviewer newReviewerAssignment = PullRequestReviewer.builder()
                .pullRequestId(pullRequestId)
                .reviewerId(newReviewer.getId())
                .build();
        
        pullRequestReviewerRepository.save(newReviewerAssignment);
        
        pullRequest = pullRequestRepository.findByIdWithReviewers(pullRequestId)
                .orElseThrow(() -> new NotFoundException("Пулл реквест не найден: " + pullRequestId));
        
        log.info("Ревьювер успешно переназначен: {} -> {} для PR {}", 
                oldUserId, newReviewer.getId(), pullRequestId);
        
        ReassignResponse response = new ReassignResponse(
                pullRequestMapper.toResponse(pullRequest),
                newReviewer.getId()
        );
        
        return response;
    }

}
