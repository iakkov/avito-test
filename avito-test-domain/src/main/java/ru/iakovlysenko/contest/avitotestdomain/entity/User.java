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
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность, представляющая пользователя.
 *
 * @author Iakov Lysenko
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_team_name", columnList = "team_name"),
    @Index(name = "idx_users_is_active", columnList = "is_active")
})
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @Column(name = "id", length = 255)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;

    /**
     * Имя пользователя.
     */
    @ToString.Include
    @Column(name = "username", nullable = false, length = 255)
    private String username;

    /**
     * Команда, к которой принадлежит пользователь.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_name", nullable = false, foreignKey = @ForeignKey(name = "fk_users_team"))
    private Team team;

    /**
     * Флаг активности пользователя.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Время, когда пользователь создан.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Время, когда пользователь обновлен.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Пулл реквесты, созданные пользователем.
     */
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PullRequest> authoredPullRequests = new ArrayList<>();

    /**
     * Назначения ревьювером на пулл реквесты.
     */
    @OneToMany(mappedBy = "reviewer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PullRequestReviewer> reviewAssignments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Получает название команды пользователя.
     *
     * @return название команды или null, если команда не установлена
     */
    public String getTeamName() {
        return team != null ? team.getTeamName() : null;
    }
}

