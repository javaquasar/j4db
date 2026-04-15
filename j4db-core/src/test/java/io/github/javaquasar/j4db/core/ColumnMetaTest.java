package io.github.javaquasar.j4db.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ColumnMeta Interface Tests")
class ColumnMetaTest {

    private enum TestColumns implements ColumnMeta {
        ID("id", Types.BIGINT, Long.class, "User ID", false),
        NAME("name", Types.VARCHAR, String.class, "User name", true);

        private final String columnName;
        private final int jdbcType;
        private final Class<?> javaType;
        private final String label;
        private final boolean nullable;

        TestColumns(String columnName, int jdbcType, Class<?> javaType, String label, boolean nullable) {
            this.columnName = columnName;
            this.jdbcType = jdbcType;
            this.javaType = javaType;
            this.label = label;
            this.nullable = nullable;
        }

        @Override
        public String columnName() { return columnName; }
        @Override
        public int jdbcType() { return jdbcType; }
        @Override
        public Class<?> javaType() { return javaType; }
        @Override
        public String label() { return label; }
        @Override
        public boolean nullable() { return nullable; }
    }

    @Test
    @DisplayName("Should return correct column metadata")
    void shouldReturnCorrectMetadata() {
        assertThat(TestColumns.ID.columnName()).isEqualTo("id");
        assertThat(TestColumns.ID.jdbcType()).isEqualTo(Types.BIGINT);
        assertThat(TestColumns.ID.javaType()).isEqualTo(Long.class);
        assertThat(TestColumns.ID.label()).isEqualTo("User ID");
        assertThat(TestColumns.ID.nullable()).isFalse();

        assertThat(TestColumns.NAME.nullable()).isTrue();
    }

    @Test
    @DisplayName("Default methods should return correct default values")
    void shouldHaveCorrectDefaultBehavior() {
        enum DefaultTest implements ColumnMeta {
            DEFAULT("default_col", Types.VARCHAR, String.class);

            private final String columnName;
            private final int jdbcType;
            private final Class<?> javaType;

            DefaultTest(String columnName, int jdbcType, Class<?> javaType) {
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

        assertThat(DefaultTest.DEFAULT.label()).isEmpty();
        assertThat(DefaultTest.DEFAULT.nullable()).isTrue();
    }
}
