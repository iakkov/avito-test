package ru.iakovlysenko.contest.service;

import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.response.GetReviewResponse;
import ru.iakovlysenko.contest.dto.response.UserResponse;

/**
 * Сервис для работы с пользователями.
 *
 * @author Iakov Lysenko
 */
public interface UserService {
    
    /**
     * Устанавливает флаг активности пользователя.
     *
     * @param request ДТО запроса на установку флага активности
     * @return ДТО ответа с информацией о пользователе
     */
    UserResponse setIsActive(SetIsActiveRequest request);
    
    /**
     * Получает список пулл реквестов, где пользователь назначен ревьювером.
     *
     * @param userId идентификатор пользователя
     * @return ДТО ответа с информацией о пулл реквестах
     */
    GetReviewResponse getReview(String userId);

}
