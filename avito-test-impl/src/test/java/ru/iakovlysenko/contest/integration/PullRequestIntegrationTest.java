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
import ru.iakovlysenko.contest.dto.request.MergePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.ReassignRequest;
import ru.iakovlysenko.contest.dto.request.TeamRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для работы с пулл реквестами.
 *
 * @author Iakov Lysenko
 */
@SpringBootTest(classes = AvitoTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Интеграционные тесты для PullRequest")
class PullRequestIntegrationTest {

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

    @BeforeEach
    void setUp() throws Exception {
        String uniqueTeamName = "PRTestTeam_" + System.currentTimeMillis();
        TeamRequest teamRequest = new TeamRequest(
                uniqueTeamName,
                java.util.List.of(
                        new TeamRequest.TeamMemberRequest("author1", "Author1", true),
                        new TeamRequest.TeamMemberRequest("reviewer1", "Reviewer1", true),
                        new TeamRequest.TeamMemberRequest("reviewer2", "Reviewer2", true)
                )
        );

        var result = mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequest)))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        if (status != 201) {
            System.out.println("Предупреждение: не удалось создать команду в setUp, статус: " + status +
                    ", ответ: " + result.getResponse().getContentAsString());
        }
    }

    @Test
    @DisplayName("Успешное создание PR с автоматическим назначением ревьюверов")
    void createPullRequest_Success() throws Exception {
        CreatePullRequestRequest request = new CreatePullRequestRequest("pr1", "PR1", "author1");

        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pr.pull_request_id").value("pr1"))
                .andExpect(jsonPath("$.pr.status").value("OPEN"))
                .andExpect(jsonPath("$.pr.assigned_reviewers").isArray())
                .andExpect(jsonPath("$.pr.assigned_reviewers.length()").value(2));
    }

    @Test
    @DisplayName("Успешное слияние PR")
    void mergePullRequest_Success() throws Exception {
        CreatePullRequestRequest createRequest = new CreatePullRequestRequest("pr2", "PR2", "author1");
        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        MergePullRequestRequest mergeRequest = new MergePullRequestRequest("pr2");

        mockMvc.perform(post("/pullRequest/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pr.status").value("MERGED"))
                .andExpect(jsonPath("$.pr.mergedAt").exists());
    }

    @Test
    @DisplayName("Идемпотентность merge - повторное слияние")
    void mergePullRequest_Idempotent() throws Exception {
        CreatePullRequestRequest createRequest = new CreatePullRequestRequest("pr3", "PR3", "author1");
        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        MergePullRequestRequest mergeRequest = new MergePullRequestRequest("pr3");

        mockMvc.perform(post("/pullRequest/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pr.status").value("MERGED"));

        mockMvc.perform(post("/pullRequest/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pr.status").value("MERGED"));
    }

    @Test
    @DisplayName("Успешное переназначение ревьювера")
    void reassignReviewer_Success() throws Exception {
        CreatePullRequestRequest createRequest = new CreatePullRequestRequest("pr4", "PR4", "author1");
        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        ReassignRequest reassignRequest = new ReassignRequest("pr4", "reviewer1");

        mockMvc.perform(post("/pullRequest/reassign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reassignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replaced_by").exists())
                .andExpect(jsonPath("$.pr.status").value("OPEN"));
    }

    @Test
    @DisplayName("Переназначение ревьювера на слитом PR должно вернуть ошибку")
    void reassignReviewer_MergedPr_Error() throws Exception {
        CreatePullRequestRequest createRequest = new CreatePullRequestRequest("pr5", "PR5", "author1");
        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        MergePullRequestRequest mergeRequest = new MergePullRequestRequest("pr5");
        mockMvc.perform(post("/pullRequest/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeRequest)))
                .andExpect(status().isOk());

        ReassignRequest reassignRequest = new ReassignRequest("pr5", "reviewer1");

        mockMvc.perform(post("/pullRequest/reassign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reassignRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("PR_MERGED"));
    }

    @Test
    @DisplayName("Создание PR с несуществующим автором должно вернуть ошибку")
    void createPullRequest_AuthorNotFound_Error() throws Exception {
        CreatePullRequestRequest request = new CreatePullRequestRequest("pr6", "PR6", "nonexistent");

        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }
}

