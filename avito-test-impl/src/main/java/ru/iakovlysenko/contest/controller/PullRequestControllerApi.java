package ru.iakovlysenko.contest.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import ru.iakovlysenko.contest.dto.request.CreatePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.MergePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.ReassignRequest;
import ru.iakovlysenko.contest.dto.response.PullRequestWrapperResponse;
import ru.iakovlysenko.contest.dto.response.ReassignResponse;

/**
 * Контроллер для работы с пулл реквестами.
 *
 * @author Iakov Lysenko
 */
public interface PullRequestControllerApi {

    /**
     * Создает пулл реквест и автоматически назначает до 2 ревьюверов из команды автора.
     *
     * @param request ДТО запроса на создание пулл реквеста
     * @return ДТО ответа с информацией о пулл реквесте
     */
    ResponseEntity<PullRequestWrapperResponse> createPullRequest(@Valid @RequestBody CreatePullRequestRequest request);

    /**
     * Помечает пулл реквест как слитый (идемпотентная операция).
     *
     * @param request ДТО запроса на слияние пулл реквеста
     * @return ДТО ответа с информацией о пулл реквесте
     */
    ResponseEntity<PullRequestWrapperResponse> mergePullRequest(@Valid @RequestBody MergePullRequestRequest request);

    /**
     * Переназначает ревьювера на другого из его команды.
     *
     * @param request ДТО запроса на переназначение ревьювера
     * @return ДТО ответа с информацией о переназначении
     */
    ResponseEntity<ReassignResponse> reassignReviewer(@Valid @RequestBody ReassignRequest request);

}
