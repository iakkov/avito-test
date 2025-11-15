package ru.iakovlysenko.contest.exception;

import ru.iakovlysenko.contest.dto.enums.ErrorCode;

public class PrMergedException extends BusinessException {
    
    public PrMergedException(String prId) {
        super(ErrorCode.PR_MERGED, "Невозможно переназначить ревьювера для слитого пулл реквеста: " + prId);
    }
}

