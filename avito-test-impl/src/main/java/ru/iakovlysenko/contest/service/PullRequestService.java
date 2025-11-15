package ru.iakovlysenko.contest.service;

import ru.iakovlysenko.contest.dto.request.CreatePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.MergePullRequestRequest;
import ru.iakovlysenko.contest.dto.request.ReassignRequest;
import ru.iakovlysenko.contest.dto.response.PullRequestResponse;
import ru.iakovlysenko.contest.dto.response.ReassignResponse;

/**
 * Сервис для работы с пулл реквестами.
 *
 * @author Iakov Lysenko
 */
public interface PullRequestService {
    
    /**
     * Создает пулл реквест и автоматически назначает до 2 ревьюверов из команды автора.
     *
     * @param request ДТО запроса на создание пулл реквеста
     * @return ДТО ответа с информацией о пулл реквесте
     */
    PullRequestResponse createPullRequest(CreatePullRequestRequest request);
    
    /**
     * Помечает пулл реквест как слитый (идемпотентная операция).
     *
     * @param request ДТО запроса на слияние пулл реквеста
     * @return ДТО ответа с информацией о пулл реквесте
     */
    PullRequestResponse mergePullRequest(MergePullRequestRequest request);
    
    /**
     * Переназначает ревьювера на другого из его команды.
     *
     * @param request ДТО запроса на переназначение ревьювера
     * @return ДТО ответа с информацией о переназначении
     */
    ReassignResponse reassignReviewer(ReassignRequest request);

}
