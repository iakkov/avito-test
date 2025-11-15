package ru.iakovlysenko.contest.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.response.GetReviewResponse;
import ru.iakovlysenko.contest.dto.response.UserWrapperResponse;

/**
 * Контроллер для работы с пользователями.
 *
 * @author Iakov Lysenko
 */
public interface UserControllerApi {

    /**
     * Устанавливает флаг активности пользователя.
     *
     * @param request ДТО запроса на установку флага активности
     * @return ДТО ответа с информацией о пользователе
     */
    ResponseEntity<UserWrapperResponse> setIsActive(@Valid @RequestBody SetIsActiveRequest request);

    /**
     * Получает список пулл реквестов, где пользователь назначен ревьювером.
     *
     * @param userId идентификатор пользователя
     * @return ДТО ответа со списком пулл реквестов
     */
    ResponseEntity<GetReviewResponse> getReview(@RequestParam("user_id") String userId);

}
