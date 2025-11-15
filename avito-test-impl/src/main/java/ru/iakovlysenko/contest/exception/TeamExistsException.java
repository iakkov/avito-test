package ru.iakovlysenko.contest.exception;

import ru.iakovlysenko.contest.dto.enums.ErrorCode;

public class TeamExistsException extends BusinessException {
    
    public TeamExistsException(String teamName) {
        super(ErrorCode.TEAM_EXISTS, "Команда с именем уже существует: " + teamName);
    }

}
