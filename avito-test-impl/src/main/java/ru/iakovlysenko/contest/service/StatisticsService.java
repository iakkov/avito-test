package ru.iakovlysenko.contest.service;

import ru.iakovlysenko.contest.dto.response.StatisticsResponse;

/**
 * Сервис для получения статистики по назначениям ревьюверов.
 */
public interface StatisticsService {

    /**
     * Возвращает агрегированную статистику по назначениям ревьюверов.
     *
     * @return ДТО ответа со статистикой
     */
    StatisticsResponse getStatistics();
}

