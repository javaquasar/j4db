package io.github.javaquasar.j4db.core;

import io.github.javaquasar.j4db.dialect.DatabaseType;
import io.github.javaquasar.j4db.dialect.DialectRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("J4dbContext Tests")
class J4dbContextTest {

    private DataSource mockDataSource;
    private DatabaseDialect testDialect;

    @BeforeEach
    void setUp() {
        mockDataSource = mock(DataSource.class);
        testDialect = new TestDialect();
        DialectRegistry.register(DatabaseType.POSTGRESQL, testDialect);
    }

    @Test
    @DisplayName("Should successfully build context with DataSource")
    void shouldBuildWithDataSource() {
        J4dbContext context = J4dbContext.builder()
                .dataSource(mockDataSource)
                .dialect(testDialect)
                .defaultSchema("public")
                .build();

        assertThat(context).isNotNull();
        assertThat(context.dialect()).isSameAs(testDialect);
        assertThat(context.defaultSchema()).isEqualTo("public");
        assertThat(context.jdbcTemplate()).isNotNull();
    }

    @Test
    @DisplayName("Should build context with pre-configured JdbcTemplate")
    void shouldBuildWithJdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(mockDataSource);

        J4dbContext context = J4dbContext.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(testDialect)
                .build();

        assertThat(context.jdbcTemplate()).isSameAs(jdbcTemplate);
    }

    @Test
    @DisplayName("Should throw exception when neither dataSource nor jdbcTemplate is provided")
    void shouldThrowWhenNoConnectionSource() {
        assertThatThrownBy(() -> J4dbContext.builder()
                .dialect(testDialect)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Either dataSource or jdbcTemplate must be provided");
    }

    @Test
    @DisplayName("Should throw when dialect is null")
    void shouldThrowWhenDialectIsNull() {
        assertThatThrownBy(() -> J4dbContext.builder()
                .dataSource(mockDataSource)
                .build())
                .isInstanceOf(NullPointerException.class);
    }

    // Simple test dialect
    private static class TestDialect implements DatabaseDialect {
        @Override
        public String getName() {
            return "test";
        }

        @Override
        public Object getValue(java.sql.ResultSet rs, String columnName, Class<?> javaType) throws java.sql.SQLException {
            return rs.getObject(columnName, javaType);
        }
    }
}
