package ru.iakovlysenko.contest.service;

import ru.iakovlysenko.contest.dto.request.TeamRequest;
import ru.iakovlysenko.contest.dto.response.TeamResponse;

/**
 * Сервис для работы с командами.
 *
 * @author Iakov Lysenko
 */
public interface TeamService {
    
    /**
     * Создает команду с участниками.
     *
     * @param request ДТО запроса на создание команды
     * @return ДТО ответа с информацией о команде
     */
    TeamResponse createTeam(TeamRequest request);
    
    /**
     * Получает информацию о команде по имени.
     *
     * @param teamName имя команды
     * @return ДТО ответа с информацией о команде
     */
    TeamResponse getTeam(String teamName);

}
