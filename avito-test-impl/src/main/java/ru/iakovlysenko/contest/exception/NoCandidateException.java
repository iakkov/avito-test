package ru.iakovlysenko.contest.exception;

import ru.iakovlysenko.contest.dto.enums.ErrorCode;

public class NoCandidateException extends BusinessException {
    
    public NoCandidateException(String message) {
        super(ErrorCode.NO_CANDIDATE, message);
    }
}

