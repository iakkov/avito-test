package ru.iakovlysenko.contest.avitotestdomain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PullRequestReviewerId implements Serializable {

    @EqualsAndHashCode.Include
    private UUID pullRequestId;

    @EqualsAndHashCode.Include
    private UUID reviewerId;

}

