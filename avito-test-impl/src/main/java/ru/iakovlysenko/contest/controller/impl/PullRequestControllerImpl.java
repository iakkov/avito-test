package ru.iakovlysenko.contest.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.iakovlysenko.contest.controller.PullRequestControllerApi;
import ru.iakovlysenko.contest.dto.request.CreatePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.MergePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.ReassignRequest;
import ru.iakovlysenko.contest.dto.response.PullRequestWrapperResponse;
import ru.iakovlysenko.contest.dto.response.ReassignResponse;
import ru.iakovlysenko.contest.service.PullRequestService;

/**
 * Реализация контроллера {@link PullRequestControllerApi}
 *
 * @author Iakov Lysenko
 */
@RestController
@RequestMapping("/pullRequest")
@Slf4j
@RequiredArgsConstructor
public class PullRequestControllerImpl implements PullRequestControllerApi {
    
    private final PullRequestService pullRequestService;
    
    @Override
    @PostMapping("/create")
    public ResponseEntity<PullRequestWrapperResponse> createPullRequest(
            @Valid @RequestBody CreatePullRequestRequest request) {
        log.info("POST /pullRequest/create - Создание пулл реквеста: {}", request.pullRequestId());
        
        var prResponse = pullRequestService.createPullRequest(request);
        
        PullRequestWrapperResponse response = new PullRequestWrapperResponse(prResponse);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    @PostMapping("/merge")
    public ResponseEntity<PullRequestWrapperResponse> mergePullRequest(
            @Valid @RequestBody MergePullRequestRequest request) {
        log.info("POST /pullRequest/merge - Слияние пулл реквеста: {}", request.pullRequestId());
        
        var prResponse = pullRequestService.mergePullRequest(request);
        
        PullRequestWrapperResponse response = new PullRequestWrapperResponse(prResponse);
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    @PostMapping("/reassign")
    public ResponseEntity<ReassignResponse> reassignReviewer(
            @Valid @RequestBody ReassignRequest request) {
        log.info("POST /pullRequest/reassign - Переназначение ревьювера {} для PR {}", 
                request.oldUserId(), request.pullRequestId());
        
        ReassignResponse response = pullRequestService.reassignReviewer(request);
        
        return ResponseEntity.ok(response);
    }

}
