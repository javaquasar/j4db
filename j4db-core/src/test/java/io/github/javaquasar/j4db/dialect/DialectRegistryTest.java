package io.github.javaquasar.j4db.dialect;

import io.github.javaquasar.j4db.core.DatabaseDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DialectRegistry}.
 *
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DialectRegistry Tests")
class DialectRegistryTest {

    @Mock
    private DatabaseDialect mockPostgresDialect;

    @Mock
    private DatabaseDialect mockOracleDialect;

    @Mock
    private DatabaseDialect mockCustomDialect;

    @BeforeEach
    void setUp() {
        // Clear registry and restore built-in dialects before each test
        DialectRegistry.clear();

        // Re-register mocks for controlled testing
        when(mockPostgresDialect.getName()).thenReturn("postgresql");
        when(mockOracleDialect.getName()).thenReturn("oracle");

        DialectRegistry.register(DatabaseType.POSTGRESQL, mockPostgresDialect);
        DialectRegistry.register(DatabaseType.ORACLE, mockOracleDialect);
    }

    @Test
    @DisplayName("Should register and retrieve built-in dialects")
    void shouldRegisterAndRetrieveBuiltInDialects() {
        assertThat(DialectRegistry.isRegistered("postgresql")).isTrue();
        assertThat(DialectRegistry.isRegistered("oracle")).isTrue();

        DatabaseDialect postgres = DialectRegistry.getDialect("postgresql");
        DatabaseDialect oracle = DialectRegistry.getDialect(DatabaseType.ORACLE);

        assertThat(postgres).isNotNull();
        assertThat(oracle).isNotNull();
        assertThat(postgres.getName()).isEqualTo("postgresql");
    }

    @Test
    @DisplayName("Should retrieve dialect case-insensitively")
    void shouldRetrieveDialectCaseInsensitively() {
        assertThat(DialectRegistry.getDialect("POSTGRESQL")).isSameAs(mockPostgresDialect);
        assertThat(DialectRegistry.getDialect("PostGreSql")).isSameAs(mockPostgresDialect);
        assertThat(DialectRegistry.getDialect("oracle")).isSameAs(mockOracleDialect);
        assertThat(DialectRegistry.getDialect("ORACLE")).isSameAs(mockOracleDialect);
    }

    @Test
    @DisplayName("Should allow overriding existing dialect")
    void shouldAllowOverridingExistingDialect() {
        // Given
        DatabaseDialect customPostgres = mock(DatabaseDialect.class);
        when(customPostgres.getName()).thenReturn("postgresql");

        // When
        DialectRegistry.register(DatabaseType.POSTGRESQL, customPostgres);

        // Then
        assertThat(DialectRegistry.getDialect("postgresql")).isSameAs(customPostgres);
        assertThat(DialectRegistry.size()).isEqualTo(2); // oracle + overridden postgres
    }

    @Test
    @DisplayName("Should register custom dialect by name")
    void shouldRegisterCustomDialectByName() {
        DialectRegistry.register("my-custom-db", mockCustomDialect);

        assertThat(DialectRegistry.isRegistered("my-custom-db")).isTrue();
        assertThat(DialectRegistry.getDialect("my-custom-db")).isSameAs(mockCustomDialect);
        assertThat(DialectRegistry.getDialect("MY-CUSTOM-DB")).isSameAs(mockCustomDialect);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for unknown dialect")
    void shouldThrowForUnknownDialect() {
        assertThatThrownBy(() -> DialectRegistry.getDialect("unknown_db"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported database type: 'unknown_db'")
                .hasMessageContaining("Available dialects:");
    }

    @Test
    @DisplayName("Should throw exception when name is null or blank")
    void shouldThrowWhenNameIsNullOrBlank() {
        assertThatThrownBy(() -> DialectRegistry.getDialect((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null or blank");

        assertThatThrownBy(() -> DialectRegistry.getDialect("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when registering with null arguments")
    void shouldThrowWhenRegisteringWithNull() {
        assertThatThrownBy(() -> DialectRegistry.register((DatabaseType) null, mockPostgresDialect))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> DialectRegistry.register(DatabaseType.POSTGRESQL, null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> DialectRegistry.register((String) null, mockPostgresDialect))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should return correct registry size")
    void shouldReturnCorrectSize() {
        assertThat(DialectRegistry.size()).isEqualTo(2);

        DialectRegistry.register("custom1", mockCustomDialect);
        assertThat(DialectRegistry.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Clear should restore built-in dialects")
    void clearShouldRestoreBuiltInDialects() {
        DialectRegistry.clear();
        assertThat(DialectRegistry.isRegistered("postgresql")).isTrue();
        assertThat(DialectRegistry.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Should support DatabaseType enum directly")
    void shouldSupportDatabaseTypeEnum() {
        DatabaseDialect dialect = DialectRegistry.getDialect(DatabaseType.POSTGRESQL);
        assertThat(dialect).isNotNull();
        assertThat(dialect.getName()).isEqualTo("postgresql");
    }
}
