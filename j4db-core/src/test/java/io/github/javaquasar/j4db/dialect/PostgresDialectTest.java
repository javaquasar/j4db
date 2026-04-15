package io.github.javaquasar.j4db.dialect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PostgresDialect}.
 *
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // UnnecessaryStubbingException
@DisplayName("PostgresDialect Tests")
class PostgresDialectTest {

    @Mock
    private ResultSet rs;

    @Mock
    private ResultSetMetaData metaData;

    private final PostgresDialect dialect = new PostgresDialect();

    @BeforeEach
    void setUp() throws SQLException {
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnType(anyInt())).thenReturn(Types.VARCHAR);
        when(metaData.getColumnTypeName(anyInt())).thenReturn("varchar");
    }

    @Test
    @DisplayName("Should return UUID when requested")
    void shouldReturnUUIDWhenRequested() throws SQLException {
        UUID expected = UUID.randomUUID();
        when(rs.getObject("user_id", UUID.class)).thenReturn(expected);

        Object value = dialect.getValue(rs, "user_id", UUID.class);

        assertThat(value).isSameAs(expected);
    }

    @Test
    @DisplayName("Should return JSON/JSONB as String")
    void shouldReturnJsonAsString() throws SQLException {
        String jsonValue = "{\"name\":\"John\",\"active\":true}";
        when(rs.getString("data")).thenReturn(jsonValue);
        when(metaData.getColumnType(anyInt())).thenReturn(Types.OTHER);   // json/jsonb

        Object value = dialect.getValue(rs, "data", String.class);

        assertThat(value).isEqualTo(jsonValue);
    }

    @Test
    @DisplayName("Should handle array types")
    void shouldHandleArrayTypes() throws SQLException {
        Object[] expected = {"tag1", "tag2", "tag3"};
        java.sql.Array sqlArray = mock(java.sql.Array.class);
        when(sqlArray.getArray()).thenReturn(expected);
        when(rs.getArray("tags")).thenReturn(sqlArray);

        Object value = dialect.getValue(rs, "tags", Object[].class);

        assertThat(value).isInstanceOf(Object[].class);
        assertThat((Object[]) value).containsExactly(expected);
    }

    @Test
    @DisplayName("Should handle network types as String")
    void shouldHandleNetworkTypesAsString() throws SQLException {
        when(rs.getString("ip_address")).thenReturn("192.168.1.1/32");
        when(metaData.getColumnTypeName(anyInt())).thenReturn("inet");

        Object value = dialect.getValue(rs, "ip_address", String.class);

        assertThat(value).isEqualTo("192.168.1.1/32");
    }

    @Test
    @DisplayName("Should delegate standard types to default getObject")
    void shouldDelegateStandardTypes() throws SQLException {
        Integer expected = 42;
        when(rs.getObject("age", Integer.class)).thenReturn(expected);

        Object value = dialect.getValue(rs, "age", Integer.class);

        assertThat(value).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return null when ResultSet returns null")
    void shouldHandleNullValues() throws SQLException {
        when(rs.getObject("missing_col", String.class)).thenReturn(null);
        when(rs.getString("missing_col")).thenReturn(null);

        Object value = dialect.getValue(rs, "missing_col", String.class);

        assertThat(value).isNull();
    }

    @Test
    @DisplayName("Should not throw when javaType is null")
    void shouldNotThrowWhenJavaTypeIsNull() throws SQLException {
        when(rs.getObject("any_col")).thenReturn("some_value");

        assertThatNoException().isThrownBy(() -> {
            Object value = dialect.getValue(rs, "any_col", null);
            assertThat(value).isEqualTo("some_value");
        });
    }
}
