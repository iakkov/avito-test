package ru.iakovlysenko.contest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.iakovlysenko.contest.entity.User;
import ru.iakovlysenko.contest.dto.response.TeamMemberResponse;

/**
 * Маппер для преобразования сущности {@link User} в ДТО участника команды.
 *
 * @author Iakov Lysenko
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeamMemberMapper {

    /**
     * Преобразует сущность пользователя в ДТО участника команды.
     *
     * @param user сущность пользователя
     * @return ДТО ответа с информацией об участнике команды
     */
    @Mapping(target = "userId", source = "id")
    TeamMemberResponse toMemberResponse(User user);

}
