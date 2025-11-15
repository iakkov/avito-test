package ru.iakovlysenko.contest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.iakovlysenko.contest.AvitoTestApplication;
import ru.iakovlysenko.contest.dto.request.CreatePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.TeamRequest;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест для эндпоинта статистики назначений ревьюверов.
 */
@SpringBootTest(classes = AvitoTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Интеграционные тесты для Statistics")
class StatisticsIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authorId;
    private String reviewerId;

    @BeforeEach
    void setUp() throws Exception {
        authorId = "stats_author_" + UUID.randomUUID();
        reviewerId = "stats_reviewer_" + UUID.randomUUID();
        String teamName = "stats_team_" + UUID.randomUUID();

        TeamRequest teamRequest = new TeamRequest(
                teamName,
                List.of(
                        new TeamRequest.TeamMemberRequest(authorId, "Author Stats", true),
                        new TeamRequest.TeamMemberRequest(reviewerId, "Reviewer Stats", true)
                )
        );

        var result = mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequest)))
                .andReturn();

        int status = result.getResponse().getStatus();
        if (status != 201) {
            throw new IllegalStateException("Не удалось подготовить данные для теста статистики, статус: "
                    + status + ", ответ: " + result.getResponse().getContentAsString());
        }
    }

    @Test
    @DisplayName("Успешное получение агрегированной статистики")
    void getStatistics_Success() throws Exception {
        String prId1 = "stats_pr_" + UUID.randomUUID();
        String prId2 = "stats_pr_" + UUID.randomUUID();

        CreatePullRequestRequest createRequest1 = new CreatePullRequestRequest(prId1, "Stats PR 1", authorId);
        CreatePullRequestRequest createRequest2 = new CreatePullRequestRequest(prId2, "Stats PR 2", authorId);

        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignments_by_user", hasSize(1)))
                .andExpect(jsonPath("$.assignments_by_user[0].user_id").value(reviewerId))
                .andExpect(jsonPath("$.assignments_by_user[0].assignments_count").value(2))
                .andExpect(jsonPath("$.reviewers_per_pr", hasSize(2)))
                .andExpect(jsonPath("$.reviewers_per_pr[?(@.pull_request_id == '" + prId1 + "')].reviewers_count",
                        contains(1)))
                .andExpect(jsonPath("$.reviewers_per_pr[?(@.pull_request_id == '" + prId2 + "')].reviewers_count",
                        contains(1)));
    }
}
