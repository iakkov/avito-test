package ru.iakovlysenko.contest.avitotestdomain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pull_request_reviewers", indexes = {
    @Index(name = "idx_pr_reviewers_reviewer_id", columnList = "reviewer_id"),
    @Index(name = "idx_pr_reviewers_pr_id", columnList = "pull_request_id")
})
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PullRequestReviewerId.class)
public class PullRequestReviewer {

    @Id
    @Column(name = "pull_request_id", nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID pullRequestId;

    @Id
    @Column(name = "reviewer_id", nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID reviewerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", nullable = false, insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_pr_reviewers_pr"))
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false, insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_pr_reviewers_user"))
    private User reviewer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}

