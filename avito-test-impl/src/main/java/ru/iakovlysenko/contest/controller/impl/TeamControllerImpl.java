package ru.iakovlysenko.contest.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.iakovlysenko.contest.controller.TeamControllerApi;
import ru.iakovlysenko.contest.dto.request.TeamRequest;
import ru.iakovlysenko.contest.dto.response.TeamResponse;
import ru.iakovlysenko.contest.dto.response.TeamWrapperResponse;
import ru.iakovlysenko.contest.service.TeamService;

/**
 * Реализация контроллера {@link TeamControllerApi}
 *
 * @author Iakov Lysenko
 */
@Slf4j
@RequiredArgsConstructor
public class TeamControllerImpl implements TeamControllerApi {
    
    private final TeamService teamService;
    
    @Override
    public ResponseEntity<TeamWrapperResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        log.info("POST /team/add - Создание команды: {}", request.teamName());
        
        TeamResponse teamResponse = teamService.createTeam(request);
        
        TeamWrapperResponse response = new TeamWrapperResponse(teamResponse);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<TeamResponse> getTeam(@RequestParam("team_name") String teamName) {
        log.info("GET /team/get - Получение команды: {}", teamName);
        
        TeamResponse teamResponse = teamService.getTeam(teamName);
        
        return ResponseEntity.ok(teamResponse);
    }
}
