package ru.iakovlysenko.contest.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.iakovlysenko.contest.entity.Team;
import ru.iakovlysenko.contest.dto.request.TeamRequest;
import ru.iakovlysenko.contest.dto.response.TeamResponse;
import ru.iakovlysenko.contest.dto.response.TeamWrapperResponse;

/**
 * Контроллер для {@link Team}
 *
 * @author Iakov Lysenko
 */
@RestController
@RequestMapping("/team")
public interface TeamControllerApi {

    @PostMapping("/add")
    ResponseEntity<TeamWrapperResponse> createTeam(@Valid @RequestBody TeamRequest request);

    @GetMapping("/get")
    ResponseEntity<TeamResponse> getTeam(@RequestParam("team_name") String teamName);

}
