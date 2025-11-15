package ru.iakovlysenko.contest.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.iakovlysenko.contest.dto.request.CreatePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.MergePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.ReassignRequest;
import ru.iakovlysenko.contest.dto.response.PullRequestResponse;
import ru.iakovlysenko.contest.dto.response.ReassignResponse;
import ru.iakovlysenko.contest.enums.PrStatus;
import ru.iakovlysenko.contest.exception.globalHandler.GlobalExceptionHandler;
import ru.iakovlysenko.contest.service.PullRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit тесты для {@link PullRequestControllerImpl}.
 *
 * @author Iakov Lysenko
 */
@WebMvcTest(controllers = PullRequestControllerImpl.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Тесты для PullRequestControllerImpl")
class PullRequestControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PullRequestService pullRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Успешное создание PR")
    void createPullRequest_Success() throws Exception {
        CreatePullRequestRequest request = new CreatePullRequestRequest("pr1", "PR1", "author1");
        PullRequestResponse prResponse = new PullRequestResponse(
                "pr1",
                "PR1",
                "author1",
                PrStatus.OPEN,
                List.of("reviewer1"),
                LocalDateTime.now(),
                null
        );

        when(pullRequestService.createPullRequest(any(CreatePullRequestRequest.class))).thenReturn(prResponse);

        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pr.pull_request_id").value("pr1"))
                .andExpect(jsonPath("$.pr.status").value("OPEN"))
                .andExpect(jsonPath("$.pr.assigned_reviewers").isArray());
    }

    @Test
    @DisplayName("Успешное слияние PR")
    void mergePullRequest_Success() throws Exception {
        MergePullRequestRequest request = new MergePullRequestRequest("pr1");
        PullRequestResponse prResponse = new PullRequestResponse(
                "pr1",
                "PR1",
                "author1",
                PrStatus.MERGED,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(pullRequestService.mergePullRequest(any(MergePullRequestRequest.class))).thenReturn(prResponse);

        mockMvc.perform(post("/pullRequest/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pr.pull_request_id").value("pr1"))
                .andExpect(jsonPath("$.pr.status").value("MERGED"))
                .andExpect(jsonPath("$.pr.mergedAt").exists());
    }

    @Test
    @DisplayName("Успешное переназначение ревьювера")
    void reassignReviewer_Success() throws Exception {
        ReassignRequest request = new ReassignRequest("pr1", "reviewer1");
        PullRequestResponse prResponse = new PullRequestResponse(
                "pr1",
                "PR1",
                "author1",
                PrStatus.OPEN,
                List.of("reviewer2"),
                LocalDateTime.now(),
                null
        );
        ReassignResponse response = new ReassignResponse(prResponse, "reviewer2");

        when(pullRequestService.reassignReviewer(any(ReassignRequest.class))).thenReturn(response);

        mockMvc.perform(post("/pullRequest/reassign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pr.pull_request_id").value("pr1"))
                .andExpect(jsonPath("$.replaced_by").value("reviewer2"));
    }

    @Test
    @DisplayName("Валидация запроса на создание PR")
    void createPullRequest_ValidationError() throws Exception {
        CreatePullRequestRequest invalidRequest = new CreatePullRequestRequest("", "", "");

        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}

