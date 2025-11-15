package ru.iakovlysenko.contest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.iakovlysenko.contest.avitotestdomain.entity.User;
import ru.iakovlysenko.contest.dto.response.UserResponse;

/**
 * Маппер для преобразования сущности {@link User} в ДТО ответа.
 *
 * @author Iakov Lysenko
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Преобразует сущность пользователя в ДТО ответа.
     *
     * @param user сущность пользователя
     * @return ДТО ответа с информацией о пользователе
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "teamName", expression = "java(user.getTeamName())")
    UserResponse toResponse(User user);

}
