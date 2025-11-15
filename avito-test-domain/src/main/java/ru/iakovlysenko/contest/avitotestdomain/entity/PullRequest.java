package ru.iakovlysenko.contest.avitotestdomain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.iakovlysenko.contest.avitotestdomain.enums.PrStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность, представляющая пулл реквест.
 *
 * @author Iakov Lysenko
 */
@Entity
@Table(name = "pull_requests", indexes = {
    @Index(name = "idx_pull_requests_author_id", columnList = "author_id"),
    @Index(name = "idx_pull_requests_status", columnList = "status")
})
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PullRequest {

    /**
     * Уникальный идентификатор.
     */
    @Id
    @Column(name = "id", length = 255)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;

    /**
     * Название пулл реквеста.
     */
    @ToString.Include
    @Column(name = "pull_request_name", nullable = false, length = 255)
    private String pullRequestName;

    /**
     * Автор пулл реквеста.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pull_requests_author"))
    private User author;

    /**
     * Статус пулл реквеста.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PrStatus status = PrStatus.OPEN;

    /**
     * Время, когда пулл реквест создан.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Время, когда пулл реквест замерджен.
     */
    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    /**
     * Ревьюверы пулл реквеста
     */
    @OneToMany(mappedBy = "pullRequest", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PullRequestReviewer> reviewers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (status == PrStatus.MERGED && mergedAt == null) {
            mergedAt = LocalDateTime.now();
        }
    }

    /**
     * Получает идентификатор автора пулл реквеста.
     *
     * @return идентификатор автора или null, если автор не установлен
     */
    public String getAuthorId() {
        return author != null ? author.getId() : null;
    }

    /**
     * Получает список идентификаторов назначенных ревьюверов.
     *
     * @return список идентификаторов ревьюверов
     */
    public List<String> getAssignedReviewerIds() {
        return reviewers.stream()
                .map(PullRequestReviewer::getReviewerId)
                .toList();
    }
}

