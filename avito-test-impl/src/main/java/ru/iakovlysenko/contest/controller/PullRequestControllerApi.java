package ru.iakovlysenko.contest.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.iakovlysenko.contest.entity.PullRequest;
import ru.iakovlysenko.contest.dto.request.CreatePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.MergePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.ReassignRequest;
import ru.iakovlysenko.contest.dto.response.PullRequestWrapperResponse;
import ru.iakovlysenko.contest.dto.response.ReassignResponse;

/**
 * Контроллер для {@link PullRequest}
 *
 * @author Iakov Lysenko
 */
@RestController
@RequestMapping("/pullRequest")
public interface PullRequestControllerApi {

    @PostMapping("/create")
    ResponseEntity<PullRequestWrapperResponse> createPullRequest(@Valid @RequestBody CreatePullRequestRequest request);

    @PostMapping("/merge")
    ResponseEntity<PullRequestWrapperResponse> mergePullRequest(@Valid @RequestBody MergePullRequestRequest request);

    @PostMapping("/reassign")
    ResponseEntity<ReassignResponse> reassignReviewer(@Valid @RequestBody ReassignRequest request);

}
