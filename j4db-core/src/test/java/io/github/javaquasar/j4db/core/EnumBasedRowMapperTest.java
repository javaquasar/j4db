package io.github.javaquasar.j4db.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnumBasedRowMapper Tests")
class EnumBasedRowMapperTest {

    // Test Record with constructor parameters
    private record TestUser(
            Long id,
            String username,
            LocalDateTime createdAt
    ) {}

    private enum TestUserColumns implements ColumnMeta {
        ID("id", Types.BIGINT, Long.class),
        USERNAME("username", Types.VARCHAR, String.class),
        CREATED_AT("created_at", Types.TIMESTAMP, LocalDateTime.class);

        private final String columnName;
        private final int jdbcType;
        private final Class<?> javaType;

        TestUserColumns(String columnName, int jdbcType, Class<?> javaType) {
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

    private static class TestUserMapper extends EnumBasedRowMapper<TestUser, TestUserColumns> {
        public TestUserMapper() {
            super(TestUser.class, () -> new TestUser(null, null, null), TestUserColumns.class);
        }

        @Override
        protected void setValue(TestUser user, TestUserColumns column, Object value) {
            // For Record-based entities we usually don't mutate
        }
    }

    @Mock
    private ResultSet resultSet;

    @Test
    @DisplayName("Should successfully instantiate mapper")
    void shouldInstantiateMapper() {
        TestUserMapper mapper = new TestUserMapper();
        assertThat(mapper).isNotNull();
        assertThat(mapper.getEntityClass()).isEqualTo(TestUser.class);
    }

    @Test
    @DisplayName("Should call getObject with correct column name and Java type")
    void shouldCallGetObjectWithCorrectParameters() throws SQLException {
        when(resultSet.getObject("id", Long.class)).thenReturn(123L);
        when(resultSet.getObject("username", String.class)).thenReturn("testuser");
        when(resultSet.getObject("created_at", LocalDateTime.class)).thenReturn(LocalDateTime.now());

        TestUserMapper mapper = new TestUserMapper();

        assertThatNoException().isThrownBy(() -> mapper.mapRow(resultSet, 0));
        verify(resultSet).getObject("id", Long.class);
        verify(resultSet).getObject("username", String.class);
        verify(resultSet).getObject("created_at", LocalDateTime.class);
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValues() throws SQLException {
        when(resultSet.getObject(anyString(), any(Class.class))).thenReturn(null);

        TestUserMapper mapper = new TestUserMapper();

        assertThatNoException().isThrownBy(() -> mapper.mapRow(resultSet, 1));
    }

    @Test
    @DisplayName("Should propagate SQLException")
    void shouldPropagateSQLException() throws SQLException {
        when(resultSet.getObject(anyString(), any(Class.class)))
                .thenThrow(new SQLException("Database error"));

        TestUserMapper mapper = new TestUserMapper();

        assertThatThrownBy(() -> mapper.mapRow(resultSet, 1))
                .isInstanceOf(SQLException.class);
    }
}
