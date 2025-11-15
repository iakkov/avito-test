package ru.iakovlysenko.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.iakovlysenko.contest.dto.enums.ErrorCode;

/**
 * ДТО ответа с информацией об ошибке.
 *
 * @author Iakov Lysenko
 */
public record ErrorResponse(
        @JsonProperty("error")
        ErrorDetail error
) {
    /**
     * ДТО деталей ошибки.
     *
     * @author Iakov Lysenko
     */
    public record ErrorDetail(
            ErrorCode code,
            String message
    ) {
    }
}
