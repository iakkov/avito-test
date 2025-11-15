package ru.iakovlysenko.contest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Конфигурация для обеспечения правильного порядка инициализации MapStruct мапперов.
 * <p>
 * Это гарантирует, что TeamMemberMapperImpl будет создан до TeamMapperImpl,
 * что предотвращает ошибки ClassNotFoundException при загрузке контекста Spring.
 *
 * @author Iakov Lysenko
 */
@Configuration
public class MapStructConfig {
}
