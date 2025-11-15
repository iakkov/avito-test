package ru.iakovlysenko.contest.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.iakovlysenko.contest.controller.UserControllerApi;
import ru.iakovlysenko.contest.dto.request.SetIsActiveRequest;
import ru.iakovlysenko.contest.dto.response.GetReviewResponse;
import ru.iakovlysenko.contest.dto.response.UserWrapperResponse;
import ru.iakovlysenko.contest.service.UserService;

/**
 * Реализация контроллера {@link UserControllerApi}
 *
 * @author Iakov Lysenko
 */
@Slf4j
@RequiredArgsConstructor
public class UserControllerImpl implements UserControllerApi {
    
    private final UserService userService;
    
    @Override
    public ResponseEntity<UserWrapperResponse> setIsActive(@Valid @RequestBody SetIsActiveRequest request) {
        log.info("POST /users/setIsActive - Setting isActive for user {} to {}", 
                request.userId(), request.isActive());
        
        var userResponse = userService.setIsActive(request);
        
        UserWrapperResponse response = new UserWrapperResponse(userResponse);
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<GetReviewResponse> getReview(@RequestParam("user_id") String userId) {
        log.info("GET /users/getReview - Getting review for user: {}", userId);
        
        GetReviewResponse response = userService.getReview(userId);
        
        return ResponseEntity.ok(response);
    }
}

