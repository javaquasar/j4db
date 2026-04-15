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
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TypedQuery Tests")
class TypedQueryTest {

    @Mock
    private J4dbContext mockContext;

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    private TypedQuery<TestUser, UserQueryParams> builder;

    private record TestUser(Long id, String name) {}

    private enum UserQueryParams implements QueryIn {
        USER_ID("userId", 1),
        STATUS("status", 2);

        private final String paramName;
        private final int position;

        UserQueryParams(String paramName, int position) {
            this.paramName = paramName;
            this.position = position;
        }

        @Override
        public String paramName() { return paramName; }
        @Override
        public int position() { return position; }
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
        when(mockContext.jdbcTemplate()).thenReturn(mockJdbcTemplate);
        builder = new TypedQuery<>(mockContext, TestUserMapper.class);
    }

    @Test
    @DisplayName("Should bind typed parameters and execute list")
    void shouldBindTypedParametersAndExecuteList() {
        List<TestUser> expected = List.of(new TestUser(1L, "Alice"));

        when(mockJdbcTemplate.query(any(String.class), any(org.springframework.jdbc.core.PreparedStatementSetter.class), any(EnumBasedRowMapper.class)))
                .thenReturn(expected);

        List<TestUser> result = builder
                .sql("SELECT * FROM users WHERE id = :userId")
                .param(UserQueryParams.USER_ID, 100L)
                .executeList();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should support executeOneOrThrow")
    void shouldSupportExecuteOneOrThrow() {
        TestUser expected = new TestUser(42L, "John");

        when(mockJdbcTemplate.query(any(String.class), any(org.springframework.jdbc.core.PreparedStatementSetter.class), any(EnumBasedRowMapper.class)))
                .thenReturn(List.of(expected));

        TestUser result = builder
                .sql("SELECT * FROM users WHERE id = :userId")
                .param(UserQueryParams.USER_ID, 42L)
                .executeOneOrThrow();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw when no result for executeOneOrThrow")
    void shouldThrowOnNoResult() {
        when(mockJdbcTemplate.query(any(String.class), any(org.springframework.jdbc.core.PreparedStatementSetter.class), any(EnumBasedRowMapper.class)))
                .thenReturn(List.of());

        assertThatThrownBy(() -> builder
                .sql("SELECT * FROM users WHERE id = :userId")
                .param(UserQueryParams.USER_ID, 999L)
                .executeOneOrThrow())
                .isInstanceOf(IllegalStateException.class);
    }
}
