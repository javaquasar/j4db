package io.github.javaquasar.j4db.dialect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("DatabaseType Enum Tests")
class DatabaseTypeTest {

    @Test
    @DisplayName("Should return correct name for each database type")
    void shouldReturnCorrectNames() {
        assertThat(DatabaseType.POSTGRESQL.getName()).isEqualTo("postgresql");
        assertThat(DatabaseType.ORACLE.getName()).isEqualTo("oracle");
        assertThat(DatabaseType.MYSQL.getName()).isEqualTo("mysql");
    }

    @Test
    @DisplayName("Should convert name to DatabaseType correctly (case insensitive)")
    void shouldConvertNameToDatabaseType() {
        assertThat(DatabaseType.fromName("postgresql")).isEqualTo(DatabaseType.POSTGRESQL);
        assertThat(DatabaseType.fromName("POSTGRESQL")).isEqualTo(DatabaseType.POSTGRESQL);
        assertThat(DatabaseType.fromName("Oracle")).isEqualTo(DatabaseType.ORACLE);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for unknown database name")
    void shouldThrowForUnknownName() {
        assertThatThrownBy(() -> DatabaseType.fromName("unknown_db"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported database type");
    }
}
