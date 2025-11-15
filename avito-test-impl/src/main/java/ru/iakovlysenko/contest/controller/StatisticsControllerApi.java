package ru.iakovlysenko.contest.controller;

import org.springframework.http.ResponseEntity;
import ru.iakovlysenko.contest.dto.response.StatisticsResponse;

/**
 * Контроллер для работы со статистикой назначений ревьюверов.
 */
public interface StatisticsControllerApi {

    /**
     * Возвращает агрегированную статистику назначений.
     *
     * @return ДТО ответа со статистикой
     */
    ResponseEntity<StatisticsResponse> getStatistics();
}
