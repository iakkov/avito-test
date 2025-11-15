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
import ru.iakovlysenko.contest.dto.request.TeamRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для работы с командами.
 *
 * @author Iakov Lysenko
 */
@SpringBootTest(classes = AvitoTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Интеграционные тесты для Team")
class TeamIntegrationTest {

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
    void setUp() {

    }

    @Test
    @DisplayName("Успешное создание команды через API")
    void createTeam_Success() throws Exception {
        TeamRequest request = new TeamRequest(
                "IntegrationTeam",
                java.util.List.of(
                        new TeamRequest.TeamMemberRequest("user1", "User1", true),
                        new TeamRequest.TeamMemberRequest("user2", "User2", true)
                )
        );

        mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.team.team_name").value("IntegrationTeam"))
                .andExpect(jsonPath("$.team.members").isArray())
                .andExpect(jsonPath("$.team.members.length()").value(2))
                .andExpect(jsonPath("$.team.members[0].user_id").value("user1"))
                .andExpect(jsonPath("$.team.members[1].user_id").value("user2"));
    }

    @Test
    @DisplayName("Успешное получение команды через API")
    void getTeam_Success() throws Exception {
        TeamRequest request = new TeamRequest(
                "IntegrationTeam2",
                java.util.List.of(
                        new TeamRequest.TeamMemberRequest("user3", "User3", true)
                )
        );

        mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/team/get")
                        .param("team_name", "IntegrationTeam2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.team_name").value("IntegrationTeam2"))
                .andExpect(jsonPath("$.members").isArray())
                .andExpect(jsonPath("$.members.length()").value(1))
                .andExpect(jsonPath("$.members[0].user_id").value("user3"));
    }

    @Test
    @DisplayName("Создание команды с существующим именем должно вернуть ошибку")
    void createTeam_DuplicateName_Error() throws Exception {
        TeamRequest request = new TeamRequest(
                "DuplicateTeam",
                java.util.List.of(
                        new TeamRequest.TeamMemberRequest("user4", "User4", true)
                )
        );

        mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("TEAM_EXISTS"));
    }

    @Test
    @DisplayName("Получение несуществующей команды должно вернуть ошибку")
    void getTeam_NotFound_Error() throws Exception {
        mockMvc.perform(get("/team/get")
                        .param("team_name", "NonExistentTeam"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

}
