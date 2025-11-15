package ru.iakovlysenko.contest.exception;

import ru.iakovlysenko.contest.dto.enums.ErrorCode;

public class NotAssignedException extends BusinessException {
    
    public NotAssignedException(String reviewerId, String prId) {
        super(ErrorCode.NOT_ASSIGNED, "Ревьювер не назначен на этот пулл реквест: reviewerId=" + reviewerId + ", prId=" + prId);
    }
}

