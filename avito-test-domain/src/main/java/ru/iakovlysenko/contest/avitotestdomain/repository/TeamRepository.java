package ru.iakovlysenko.contest.avitotestdomain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.iakovlysenko.contest.avitotestdomain.entity.Team;

import java.util.Optional;

/**
 * Репозиторий для {@link Team}
 *
 * @author Iakov Lysenko
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, String> {

    /**
     * Поиск команды по имени
     *
     * @param teamName имя команды
     * @return команда, если найдена
     */
    Optional<Team> findByTeamName(String teamName);

    /**
     * Проверка существования команды по имени
     *
     * @param teamName имя команды
     * @return true, если команда существует
     */
    boolean existsByTeamName(String teamName);
}

