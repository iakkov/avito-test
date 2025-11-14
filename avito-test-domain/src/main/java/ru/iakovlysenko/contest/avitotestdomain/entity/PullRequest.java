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
import java.util.UUID;

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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ToString.Include
    @Column(name = "pull_request_name", nullable = false, length = 255)
    private String pullRequestName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pull_requests_author"))
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PrStatus status = PrStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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

    public UUID getAuthorId() {
        return author != null ? author.getId() : null;
    }

    public List<UUID> getAssignedReviewerIds() {
        return reviewers.stream()
                .map(PullRequestReviewer::getReviewerId)
                .toList();
    }
}

