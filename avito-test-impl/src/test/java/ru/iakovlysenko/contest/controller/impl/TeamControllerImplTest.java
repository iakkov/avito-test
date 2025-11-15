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
import ru.iakovlysenko.contest.dto.request.TeamRequest;
import ru.iakovlysenko.contest.dto.response.TeamMemberResponse;
import ru.iakovlysenko.contest.dto.response.TeamResponse;
import ru.iakovlysenko.contest.exception.globalHandler.GlobalExceptionHandler;
import ru.iakovlysenko.contest.service.TeamService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit тесты для {@link TeamControllerImpl}.
 *
 * @author Iakov Lysenko
 */
@WebMvcTest(controllers = TeamControllerImpl.class)
@ContextConfiguration(classes = TestApplication.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Тесты для TeamControllerImpl")
class TeamControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Успешное создание команды")
    void createTeam_Success() throws Exception {
        TeamRequest request = new TeamRequest(
                "TestTeam",
                List.of(
                        new TeamRequest.TeamMemberRequest("user1", "User1", true),
                        new TeamRequest.TeamMemberRequest("user2", "User2", true)
                )
        );

        TeamResponse teamResponse = new TeamResponse(
                "TestTeam",
                List.of(
                        new TeamMemberResponse("user1", "User1", true),
                        new TeamMemberResponse("user2", "User2", true)
                )
        );

        when(teamService.createTeam(any(TeamRequest.class))).thenReturn(teamResponse);

        mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.team.team_name").value("TestTeam"))
                .andExpect(jsonPath("$.team.members").isArray())
                .andExpect(jsonPath("$.team.members.length()").value(2));
    }

    @Test
    @DisplayName("Успешное получение команды")
    void getTeam_Success() throws Exception {
        TeamResponse teamResponse = new TeamResponse(
                "TestTeam",
                List.of(
                        new TeamMemberResponse("user1", "User1", true),
                        new TeamMemberResponse("user2", "User2", true)
                )
        );

        when(teamService.getTeam("TestTeam")).thenReturn(teamResponse);

        mockMvc.perform(get("/team/get")
                        .param("team_name", "TestTeam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.team_name").value("TestTeam"))
                .andExpect(jsonPath("$.members").isArray())
                .andExpect(jsonPath("$.members.length()").value(2));
    }

    @Test
    @DisplayName("Валидация запроса на создание команды")
    void createTeam_ValidationError() throws Exception {
        TeamRequest invalidRequest = new TeamRequest(
                "",
                List.of()
        );

        mockMvc.perform(post("/team/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
