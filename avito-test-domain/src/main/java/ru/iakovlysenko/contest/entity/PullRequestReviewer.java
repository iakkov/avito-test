package ru.iakovlysenko.contest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая назначение ревьювера на пулл реквест.
 *
 * @author Iakov Lysenko
 */
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

    /**
     * Идентификатор пулл реквеста.
     */
    @Id
    @Column(name = "pull_request_id", nullable = false, length = 255)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String pullRequestId;

    /**
     * Идентификатор ревьювера.
     */
    @Id
    @Column(name = "reviewer_id", nullable = false, length = 255)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String reviewerId;

    /**
     * Пулл реквест, на который назначен ревьювер.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", nullable = false, insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_pr_reviewers_pr"))
    private PullRequest pullRequest;

    /**
     * Ревьювер, назначенный на пулл реквест.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false, insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_pr_reviewers_user"))
    private User reviewer;

    /**
     * Время, когда ревьювер назначен на пулл реквест.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}

