package io.github.javaquasar.j4db.core.query;

import io.github.javaquasar.j4db.core.ColumnMeta;
import io.github.javaquasar.j4db.core.EnumBasedRowMapper;
import io.github.javaquasar.j4db.core.J4dbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RawQuery Tests")
class RawQueryTest {

    @Mock
    private J4dbContext mockContext;

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    private RawQuery<TestUser> builder;

    private record TestUser(Long id, String name) {
    }

    private static class TestUserMapper extends EnumBasedRowMapper<TestUser, TestColumns> {
        public TestUserMapper() {
            super(TestUser.class, () -> new TestUser(null, null), TestColumns.class);
        }

        @Override
        protected void setValue(TestUser user, TestColumns column, Object value) {}
    }

    private enum TestColumns implements ColumnMeta {
        ID("id", java.sql.Types.BIGINT, Long.class),
        NAME("name", java.sql.Types.VARCHAR, String.class);

        private final String columnName;
        private final int jdbcType;
        private final Class<?> javaType;

        TestColumns(String columnName, int jdbcType, Class<?> javaType) {
            this.columnName = columnName;
            this.jdbcType = jdbcType;
            this.javaType = javaType;
        }

        @Override
        public String columnName() { return columnName; }
        @Override
        public int jdbcType() { return jdbcType; }
        @Override
        public Class<?> javaType() { return javaType; }
    }

    @BeforeEach
    void setUp() {
        // Lenient stubbing to avoid UnnecessaryStubbingException
        lenient().when(mockContext.jdbcTemplate()).thenReturn(mockJdbcTemplate);
        builder = new RawQuery<>(mockContext, TestUserMapper.class);
    }

    @Test
    @DisplayName("Should execute list query with parameters")
    void shouldExecuteListWithParameters() {
        List<TestUser> expected = List.of(new TestUser(1L, "Alice"));

        when(mockJdbcTemplate.query(anyString(), any(Object[].class), any(EnumBasedRowMapper.class)))
                .thenReturn(expected);

        List<TestUser> result = builder
                .sql("SELECT * FROM users WHERE active = ?")
                .param(true)
                .executeList();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should execute single result with executeOne()")
    void shouldExecuteOneSuccessfully() {
        TestUser expected = new TestUser(42L, "John");

        when(mockJdbcTemplate.query(anyString(), any(Object[].class), any(EnumBasedRowMapper.class)))
                .thenReturn(List.of(expected));

        TestUser result = builder
                .sql("SELECT * FROM users WHERE id = ?")
                .param(42L)
                .executeOne();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return null when no rows found")
    void shouldReturnNullWhenNoRows() {
        when(mockJdbcTemplate.query(anyString(), any(Object[].class), any(EnumBasedRowMapper.class)))
                .thenReturn(List.of());

        TestUser result = builder
                .sql("SELECT * FROM users WHERE id = ?")
                .param(999L)
                .executeOne();

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should throw exception when SQL is not provided")
    void shouldThrowWhenSqlIsMissing() {
        assertThatThrownBy(() -> builder.executeList())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("SQL must be provided");
    }
}
