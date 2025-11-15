package ru.iakovlysenko.contest.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.iakovlysenko.contest.dto.request.TeamRequest;
import ru.iakovlysenko.contest.dto.response.TeamResponse;
import ru.iakovlysenko.contest.dto.response.TeamWrapperResponse;

/**
 * Контроллер для работы с командами.
 *
 * @author Iakov Lysenko
 */
public interface TeamControllerApi {

    /**
     * Создает команду с участниками.
     *
     * @param request ДТО запроса на создание команды
     * @return ДТО ответа с информацией о команде
     */
    ResponseEntity<TeamWrapperResponse> createTeam(@Valid @RequestBody TeamRequest request);

    /**
     * Получает информацию о команде по имени.
     *
     * @param teamName имя команды
     * @return ДТО ответа с информацией о команде
     */
    ResponseEntity<TeamResponse> getTeam(@RequestParam("team_name") String teamName);

}
