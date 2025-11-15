package ru.iakovlysenko.contest.avitotestdomain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.iakovlysenko.contest.avitotestdomain.entity.Team;
import ru.iakovlysenko.contest.avitotestdomain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для {@link User}
 *
 * @author Iakov Lysenko
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Поиск пользователя по ID
     *
     * @param id идентификатор пользователя
     * @return пользователь, если найден
     */
    Optional<User> findById(String id);

    /**
     * Проверка существования пользователя по ID
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь существует
     */
    boolean existsById(String id);

    /**
     * Поиск всех пользователей команды по имени команды.
     *
     * @param teamName имя команды
     * @return список всех пользователей команды
     */
    @Query("SELECT u FROM User u WHERE u.team.teamName = :teamName")
    List<User> findByTeamName(@Param("teamName") String teamName);

    /**
     * Поиск активных пользователей команды, исключая указанного пользователя.
     * Используется для выбора ревьюверов при создании PR (исключая автора).
     *
     * @param team команда
     * @param excludeUserId идентификатор пользователя для исключения (обычно автор PR)
     * @return список активных пользователей команды, кроме указанного
     */
    @Query("SELECT u FROM User u WHERE u.team = :team AND u.isActive = true AND u.id != :excludeUserId")
    List<User> findActiveUsersByTeamExcludingUser(@Param("team") Team team, @Param("excludeUserId") String excludeUserId);

    /**
     * Поиск активных пользователей той же команды, что и указанный пользователь, исключая самого пользователя.
     * Используется для переназначения ревьювера.
     *
     * @param userId идентификатор пользователя (заменяемого ревьювера)
     * @return список активных пользователей той же команды, кроме указанного
     */
    @Query("SELECT u FROM User u WHERE u.team = (SELECT u2.team FROM User u2 WHERE u2.id = :userId) AND u.isActive = true AND u.id != :userId")
    List<User> findActiveTeamMembersExcludingUser(@Param("userId") String userId);
}

