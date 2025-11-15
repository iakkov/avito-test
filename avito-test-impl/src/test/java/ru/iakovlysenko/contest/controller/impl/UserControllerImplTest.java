package ru.iakovlysenko.contest.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.response.GetReviewResponse;
import ru.iakovlysenko.contest.dto.response.PullRequestShortResponse;
import ru.iakovlysenko.contest.dto.response.UserResponse;
import ru.iakovlysenko.contest.enums.PrStatus;
import ru.iakovlysenko.contest.exception.globalHandler.GlobalExceptionHandler;
import ru.iakovlysenko.contest.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit тесты для {@link UserControllerImpl}.
 *
 * @author Iakov Lysenko
 */
@WebMvcTest(controllers = UserControllerImpl.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Тесты для UserControllerImpl")
class UserControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Успешная установка флага активности")
    void setIsActive_Success() throws Exception {
        SetIsActiveRequest request = new SetIsActiveRequest("user1", false);
        UserResponse userResponse = new UserResponse("user1", "User1", "Team1", false);

        when(userService.setIsActive(any(SetIsActiveRequest.class))).thenReturn(userResponse);

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
        GetReviewResponse response = new GetReviewResponse(
                "user1",
                List.of(
                        new PullRequestShortResponse("pr1", "PR1", "author1", PrStatus.OPEN),
                        new PullRequestShortResponse("pr2", "PR2", "author2", PrStatus.OPEN)
                )
        );

        when(userService.getReview("user1")).thenReturn(response);

        mockMvc.perform(get("/users/getReview")
                        .param("user_id", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value("user1"))
                .andExpect(jsonPath("$.pull_requests").isArray())
                .andExpect(jsonPath("$.pull_requests.length()").value(2));
    }

    @Test
    @DisplayName("Валидация запроса на установку флага активности")
    void setIsActive_ValidationError() throws Exception {
        SetIsActiveRequest invalidRequest = new SetIsActiveRequest("", null);

        mockMvc.perform(post("/users/setIsActive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}
