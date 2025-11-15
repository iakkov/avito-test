package ru.iakovlysenko.contest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.iakovlysenko.contest.entity.Team;
import ru.iakovlysenko.contest.dto.response.TeamResponse;

/**
 * Маппер для преобразования сущности {@link Team} в ДТО ответа.
 *
 * @author Iakov Lysenko
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {TeamMemberMapper.class})
public interface TeamMapper {

    /**
     * Преобразует сущность команды в ДТО ответа.
     *
     * @param team сущность команды
     * @return ДТО ответа с информацией о команде
     */
    TeamResponse toResponse(Team team);

}