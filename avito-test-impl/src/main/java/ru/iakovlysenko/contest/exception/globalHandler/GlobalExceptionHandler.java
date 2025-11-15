package ru.iakovlysenko.contest.exception.globalHandler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.iakovlysenko.contest.dto.enums.ErrorCode;
import ru.iakovlysenko.contest.dto.response.ErrorResponse;
import ru.iakovlysenko.contest.exception.BusinessException;
import ru.iakovlysenko.contest.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("Бизнес-исключение: {}", e.getMessage());
        
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus = getHttpStatus(errorCode);
        
        ErrorResponse response = new ErrorResponse(
                new ErrorResponse.ErrorDetail(errorCode, e.getMessage())
        );
        
        return ResponseEntity.status(httpStatus).body(response);
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.warn("Исключение: не найдено: {}", e.getMessage());
        
        ErrorResponse response = new ErrorResponse(
                new ErrorResponse.ErrorDetail(ErrorCode.NOT_FOUND, e.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Исключение валидации: {}", e.getMessage());
        
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Валидация не пройдена");
        
        ErrorResponse response = new ErrorResponse(
                new ErrorResponse.ErrorDetail(ErrorCode.NOT_FOUND, message)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Исключение нарушения ограничений: {}", e.getMessage());
        
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Валидация не пройдена");
        
        ErrorResponse response = new ErrorResponse(
                new ErrorResponse.ErrorDetail(ErrorCode.NOT_FOUND, message)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Неожиданное исключение", e);
        
        ErrorResponse response = new ErrorResponse(
                new ErrorResponse.ErrorDetail(ErrorCode.NOT_FOUND, "Внутренняя ошибка сервера")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case TEAM_EXISTS, PR_EXISTS -> HttpStatus.BAD_REQUEST;
            case PR_MERGED, NOT_ASSIGNED, NO_CANDIDATE -> HttpStatus.CONFLICT;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }
}
