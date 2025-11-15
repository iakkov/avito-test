package ru.iakovlysenko.contest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.iakovlysenko.contest.entity.PullRequest;
import ru.iakovlysenko.contest.dto.response.PullRequestResponse;
import ru.iakovlysenko.contest.dto.response.PullRequestShortResponse;

/**
 * Маппер для преобразования сущности {@link PullRequest} в ДТО ответов.
 *
 * @author Iakov Lysenko
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PullRequestMapper {

    /**
     * Преобразует сущность пулл реквеста в полный ДТО ответа.
     *
     * @param pullRequest сущность пулл реквеста
     * @return ДТО ответа с полной информацией о пулл реквесте
     */
    @Mapping(target = "pullRequestId", source = "id")
    @Mapping(target = "authorId", expression = "java(pullRequest.getAuthorId())")
    @Mapping(target = "assignedReviewers", expression = "java(pullRequest.getAssignedReviewerIds())")
    PullRequestResponse toResponse(PullRequest pullRequest);

    /**
     * Преобразует сущность пулл реквеста в краткий ДТО ответа.
     *
     * @param pullRequest сущность пулл реквеста
     * @return ДТО ответа с краткой информацией о пулл реквесте
     */
    @Mapping(target = "pullRequestId", source = "id")
    @Mapping(target = "authorId", expression = "java(pullRequest.getAuthorId())")
    PullRequestShortResponse toShortResponse(PullRequest pullRequest);

}
