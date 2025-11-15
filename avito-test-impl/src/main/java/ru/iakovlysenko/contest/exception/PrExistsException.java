package ru.iakovlysenko.contest.exception;

import ru.iakovlysenko.contest.dto.enums.ErrorCode;

public class PrExistsException extends BusinessException {
    
    public PrExistsException(String prId) {
        super(ErrorCode.PR_EXISTS, "Пулл реквест с таким ID уже существует: " + prId);
    }
}

