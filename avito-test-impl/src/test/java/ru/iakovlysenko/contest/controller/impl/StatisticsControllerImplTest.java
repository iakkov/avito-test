package ru.iakovlysenko.contest.controller.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.iakovlysenko.contest.dto.response.StatisticsResponse;
import ru.iakovlysenko.contest.exception.globalHandler.GlobalExceptionHandler;
import ru.iakovlysenko.contest.service.StatisticsService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit тесты для {@link StatisticsControllerImpl}.
 *
 * @author Iakov Lysenko
 */
@WebMvcTest(controllers = StatisticsControllerImpl.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Тесты для StatisticsControllerImpl")
class StatisticsControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    @DisplayName("Успешное получение статистики")
    void getStatistics_Success() throws Exception {
        StatisticsResponse response = new StatisticsResponse(
                List.of(
                        new StatisticsResponse.UserAssignmentsStatistic("user1", 5L),
                        new StatisticsResponse.UserAssignmentsStatistic("user2", 3L)
                ),
                List.of(
                        new StatisticsResponse.PullRequestReviewersStatistic("pr1", 2L),
                        new StatisticsResponse.PullRequestReviewersStatistic("pr2", 1L)
                )
        );

        when(statisticsService.getStatistics()).thenReturn(response);

        mockMvc.perform(get("/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignments_by_user").isArray())
                .andExpect(jsonPath("$.assignments_by_user.length()").value(2))
                .andExpect(jsonPath("$.assignments_by_user[0].user_id").value("user1"))
                .andExpect(jsonPath("$.assignments_by_user[0].assignments_count").value(5))
                .andExpect(jsonPath("$.assignments_by_user[1].user_id").value("user2"))
                .andExpect(jsonPath("$.assignments_by_user[1].assignments_count").value(3))
                .andExpect(jsonPath("$.reviewers_per_pr").isArray())
                .andExpect(jsonPath("$.reviewers_per_pr.length()").value(2))
                .andExpect(jsonPath("$.reviewers_per_pr[0].pull_request_id").value("pr1"))
                .andExpect(jsonPath("$.reviewers_per_pr[0].reviewers_count").value(2))
                .andExpect(jsonPath("$.reviewers_per_pr[1].pull_request_id").value("pr2"))
                .andExpect(jsonPath("$.reviewers_per_pr[1].reviewers_count").value(1));
    }

    @Test
    @DisplayName("Получение статистики с пустыми данными")
    void getStatistics_EmptyData_Success() throws Exception {
        StatisticsResponse response = new StatisticsResponse(
                List.of(),
                List.of()
        );

        when(statisticsService.getStatistics()).thenReturn(response);

        mockMvc.perform(get("/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignments_by_user").isArray())
                .andExpect(jsonPath("$.assignments_by_user.length()").value(0))
                .andExpect(jsonPath("$.reviewers_per_pr").isArray())
                .andExpect(jsonPath("$.reviewers_per_pr.length()").value(0));
    }

    @Test
    @DisplayName("Получение статистики только с назначениями по пользователям")
    void getStatistics_OnlyAssignments_Success() throws Exception {
        StatisticsResponse response = new StatisticsResponse(
                List.of(
                        new StatisticsResponse.UserAssignmentsStatistic("user1", 10L),
                        new StatisticsResponse.UserAssignmentsStatistic("user2", 7L),
                        new StatisticsResponse.UserAssignmentsStatistic("user3", 2L)
                ),
                List.of()
        );

        when(statisticsService.getStatistics()).thenReturn(response);

        mockMvc.perform(get("/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignments_by_user").isArray())
                .andExpect(jsonPath("$.assignments_by_user.length()").value(3))
                .andExpect(jsonPath("$.assignments_by_user[0].user_id").value("user1"))
                .andExpect(jsonPath("$.assignments_by_user[0].assignments_count").value(10))
                .andExpect(jsonPath("$.reviewers_per_pr").isArray())
                .andExpect(jsonPath("$.reviewers_per_pr.length()").value(0));
    }

    @Test
    @DisplayName("Получение статистики только с количеством ревьюверов по PR")
    void getStatistics_OnlyReviewersCount_Success() throws Exception {
        StatisticsResponse response = new StatisticsResponse(
                List.of(),
                List.of(
                        new StatisticsResponse.PullRequestReviewersStatistic("pr1", 2L),
                        new StatisticsResponse.PullRequestReviewersStatistic("pr2", 2L),
                        new StatisticsResponse.PullRequestReviewersStatistic("pr3", 1L)
                )
        );

        when(statisticsService.getStatistics()).thenReturn(response);

        mockMvc.perform(get("/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignments_by_user").isArray())
                .andExpect(jsonPath("$.assignments_by_user.length()").value(0))
                .andExpect(jsonPath("$.reviewers_per_pr").isArray())
                .andExpect(jsonPath("$.reviewers_per_pr.length()").value(3))
                .andExpect(jsonPath("$.reviewers_per_pr[0].pull_request_id").value("pr1"))
                .andExpect(jsonPath("$.reviewers_per_pr[0].reviewers_count").value(2))
                .andExpect(jsonPath("$.reviewers_per_pr[1].pull_request_id").value("pr2"))
                .andExpect(jsonPath("$.reviewers_per_pr[1].reviewers_count").value(2))
                .andExpect(jsonPath("$.reviewers_per_pr[2].pull_request_id").value("pr3"))
                .andExpect(jsonPath("$.reviewers_per_pr[2].reviewers_count").value(1));
    }

    @Test
    @DisplayName("Проверка структуры JSON ответа")
    void getStatistics_JsonStructure_Success() throws Exception {
        StatisticsResponse response = new StatisticsResponse(
                List.of(new StatisticsResponse.UserAssignmentsStatistic("user1", 5L)),
                List.of(new StatisticsResponse.PullRequestReviewersStatistic("pr1", 2L))
        );

        when(statisticsService.getStatistics()).thenReturn(response);

        mockMvc.perform(get("/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assignments_by_user[0].user_id").exists())
                .andExpect(jsonPath("$.assignments_by_user[0].assignments_count").exists())
                .andExpect(jsonPath("$.reviewers_per_pr[0].pull_request_id").exists())
                .andExpect(jsonPath("$.reviewers_per_pr[0].reviewers_count").exists());
    }

}
