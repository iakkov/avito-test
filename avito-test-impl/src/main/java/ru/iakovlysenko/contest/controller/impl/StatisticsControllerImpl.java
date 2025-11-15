package ru.iakovlysenko.contest.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.iakovlysenko.contest.controller.StatisticsControllerApi;
import ru.iakovlysenko.contest.dto.response.StatisticsResponse;
import ru.iakovlysenko.contest.service.StatisticsService;

/**
 * Реализация контроллера {@link StatisticsControllerApi}.
 */
@RestController
@RequestMapping("/statistics")
@Slf4j
@RequiredArgsConstructor
public class StatisticsControllerImpl implements StatisticsControllerApi {

    private final StatisticsService statisticsService;

    @Override
    @GetMapping
    public ResponseEntity<StatisticsResponse> getStatistics() {
        log.info("GET /statistics - Получение статистики назначений");

        StatisticsResponse response = statisticsService.getStatistics();

        return ResponseEntity.ok(response);
    }
}
