package ru.iakovlysenko.contest.exception;

import lombok.Getter;
import ru.iakovlysenko.contest.dto.enums.ErrorCode;

@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

