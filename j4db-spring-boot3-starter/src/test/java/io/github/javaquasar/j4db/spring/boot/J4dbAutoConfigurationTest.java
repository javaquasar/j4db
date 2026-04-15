package io.github.javaquasar.j4db.spring.boot;

import io.github.javaquasar.j4db.core.J4dbContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;

/**
 * J4dbAutoConfiguration Tests
 */
@DisplayName("J4dbAutoConfiguration Tests")
class J4dbAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(J4dbAutoConfiguration.class));

    @Test
    @DisplayName("Should create J4dbContext bean when DataSource is provided")
    void shouldCreateJ4dbContextBean() {
        contextRunner
                .withBean(DataSource.class, this::createTestDataSource)
                .run(context -> {
                    assertThat(context).hasSingleBean(J4dbContext.class);

                    J4dbContext ctx = context.getBean(J4dbContext.class);
                    assertThat(ctx).isNotNull();
                    assertThat(ctx.jdbcTemplate()).isNotNull();
                    assertThat(ctx.dialect()).isNotNull();
                });
    }

    @Test
    @DisplayName("Should respect j4db.default-schema property")
    void shouldRespectDefaultSchemaProperty() {
        contextRunner
                .withPropertyValues("j4db.default-schema=test_schema")
                .withBean(DataSource.class, this::createTestDataSource)
                .run(context -> {
                    J4dbContext ctx = context.getBean(J4dbContext.class);
                    assertThat(ctx.defaultSchema()).isEqualTo("test_schema");
                });
    }

    private DataSource createTestDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }
}
