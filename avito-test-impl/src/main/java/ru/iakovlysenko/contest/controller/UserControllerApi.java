package ru.iakovlysenko.contest.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.iakovlysenko.contest.entity.User;
import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.response.GetReviewResponse;
import ru.iakovlysenko.contest.dto.response.UserWrapperResponse;

/**
 * Контроллер для {@link User}
 *
 * @author Iakov Lysenko
 */
@RestController
@RequestMapping("/users")
public interface UserControllerApi {

    @PostMapping("/setIsActive")
    ResponseEntity<UserWrapperResponse> setIsActive(@Valid @RequestBody SetIsActiveRequest request);

    @GetMapping("/getReview")
    ResponseEntity<GetReviewResponse> getReview(@RequestParam("user_id") String userId);

}
