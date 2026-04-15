package io.github.javaquasar.j4db.spring.boot;

import io.github.javaquasar.j4db.core.DatabaseDialect;
import io.github.javaquasar.j4db.core.J4dbContext;
import io.github.javaquasar.j4db.dialect.DialectRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * Auto-configuration for J4DB.
 *
 * <p>Provides:
 * <ul>
 *   <li>{@link J4dbProperties} binding</li>
 *   <li>Default lightweight {@link DatabaseDialect}</li>
 *   <li>{@link J4dbContext} bean</li>
 * </ul>
 *
 * <p>Users can override the dialect by declaring their own {@code @Bean DatabaseDialect}.
 *
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(J4dbProperties.class)
public class J4dbAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DatabaseDialect databaseDialect(J4dbProperties properties) {
        String name = (properties.getDialect() != null && !properties.getDialect().isBlank())
                ? properties.getDialect()
                : "postgresql";
        return DialectRegistry.getDialect(name);
    }

    @Bean
    @ConditionalOnMissingBean
    public J4dbContext j4dbContext(DataSource dataSource, DatabaseDialect dialect, J4dbProperties properties) {
        return J4dbContext.builder()
                .dataSource(dataSource)
                .dialect(dialect)
                .defaultSchema(properties.getDefaultSchema())
                .build();
    }
}

