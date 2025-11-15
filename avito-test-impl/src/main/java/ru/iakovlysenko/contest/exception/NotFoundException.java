package ru.iakovlysenko.contest.exception;

import ru.iakovlysenko.contest.dto.enums.ErrorCode;

public class NotFoundException extends BusinessException {
    
    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}

