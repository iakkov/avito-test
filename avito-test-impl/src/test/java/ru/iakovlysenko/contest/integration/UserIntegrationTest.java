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
import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.request.TeamRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для работы с пользователями.
 *
 * @author Iakov Lysenko
 */
@SpringBootTest(classes = AvitoTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Интеграционные тесты для User")
class UserIntegrationTest {

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

        String uniqueTeamName = "UserTestTeam_" + System.currentTimeMillis();
        TeamRequest teamRequest = new TeamRequest(
                uniqueTeamName,
                java.util.List.of(
                        new TeamRequest.TeamMemberRequest("user1", "User1", true),
                        new TeamRequest.TeamMemberRequest("user2", "User2", true)
                )
        );

        var result = mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequest)))
                .andReturn();
        
       if (result.getResponse().getStatus() != 201 && result.getResponse().getStatus() != 400) {
            throw new RuntimeException("Не удалось создать команду в setUp: " + result.getResponse().getContentAsString());
        }
    }

    @Test
    @DisplayName("Успешная установка флага активности пользователя")
    void setIsActive_Success() throws Exception {
        SetIsActiveRequest request = new SetIsActiveRequest("user1", false);

        mockMvc.perform(post("/users/setIsActive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.user_id").value("user1"))
                .andExpect(jsonPath("$.user.is_active").value(false));
    }

    @Test
    @DisplayName("Успешное получение списка PR для ревьювера")
    void getReview_Success() throws Exception {
        CreatePullRequestRequest createRequest = new CreatePullRequestRequest("pr1", "PR1", "user1");
        mockMvc.perform(post("/pullRequest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users/getReview")
                        .param("user_id", "user2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value("user2"))
                .andExpect(jsonPath("$.pull_requests").isArray())
                .andExpect(jsonPath("$.pull_requests.length()").value(1))
                .andExpect(jsonPath("$.pull_requests[0].pull_request_id").value("pr1"));
    }

    @Test
    @DisplayName("Установка флага активности для несуществующего пользователя должна вернуть ошибку")
    void setIsActive_UserNotFound_Error() throws Exception {
        SetIsActiveRequest request = new SetIsActiveRequest("nonexistent", false);

        mockMvc.perform(post("/users/setIsActive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("Получение списка PR для несуществующего пользователя должно вернуть ошибку")
    void getReview_UserNotFound_Error() throws Exception {
        mockMvc.perform(get("/users/getReview")
                        .param("user_id", "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

}
