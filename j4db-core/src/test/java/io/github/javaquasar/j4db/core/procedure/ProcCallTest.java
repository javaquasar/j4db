package io.github.javaquasar.j4db.core.procedure;

import io.github.javaquasar.j4db.core.DatabaseDialect;
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

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ProcCall Tests")
class ProcCallTest {

    @Mock
    private J4dbContext mockContext;

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    private TestProc testProc;

    // Test procedure
    private static class TestProc extends EnumBasedProc<TestProc.In, TestProc.Out> {

        public TestProc() {
            super("public.get_user_info", new TestDialect());
        }

        public enum In implements ProcIn {
            USER_ID("user_id", java.sql.Types.BIGINT, Long.class, ProcParamDirection.IN);

            private final String paramName;
            private final int jdbcType;
            private final Class<?> javaType;
            private final ProcParamDirection direction;

            In(String paramName, int jdbcType, Class<?> javaType, ProcParamDirection direction) {
                this.paramName = paramName;
                this.jdbcType = jdbcType;
                this.javaType = javaType;
                this.direction = direction;
            }

            @Override
            public String paramName() { return paramName; }
            @Override
            public int jdbcType() { return jdbcType; }
            @Override
            public Class<?> javaType() { return javaType; }
            @Override
            public ProcParamDirection direction() { return direction; }
        }

        public enum Out implements ProcOut {
            USER_NAME("user_name", java.sql.Types.VARCHAR, String.class, ProcParamDirection.OUT),
            BALANCE("balance", java.sql.Types.NUMERIC, BigDecimal.class, ProcParamDirection.OUT);

            private final String paramName;
            private final int jdbcType;
            private final Class<?> javaType;
            private final ProcParamDirection direction;

            Out(String paramName, int jdbcType, Class<?> javaType, ProcParamDirection direction) {
                this.paramName = paramName;
                this.jdbcType = jdbcType;
                this.javaType = javaType;
                this.direction = direction;
            }

            @Override
            public String paramName() { return paramName; }
            @Override
            public int jdbcType() { return jdbcType; }
            @Override
            public Class<?> javaType() { return javaType; }
            @Override
            public ProcParamDirection direction() { return direction; }
        }

        @Override
        protected void registerOutParameters(java.sql.CallableStatement cs) {
            // Stub for test
        }
    }

    // Local test dialect
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

    @BeforeEach
    void setUp() {
        when(mockContext.jdbcTemplate()).thenReturn(mockJdbcTemplate);
        testProc = new TestProc();
    }

    @Test
    @DisplayName("Should build StoredProcCall and execute successfully")
    void shouldExecuteStoredProcCall() {
        // Explicitly specify the generic type to avoid ambiguity
        when(mockJdbcTemplate.execute(
                any(org.springframework.jdbc.core.CallableStatementCreator.class),
                any(org.springframework.jdbc.core.CallableStatementCallback.class)))
                .thenReturn(new ProcResult<>(Map.of(
                        "user_name", "john_doe",
                        "balance", new BigDecimal("1250.50")
                )));

        ProcResult<TestProc.Out> result = testProc.call()
                .with(TestProc.In.USER_ID, 123L)
                .execute(mockContext);

        assertThat(result).isNotNull();
        // Use explicit casting or typed assert to avoid ambiguity
        String userName = result.get(TestProc.Out.USER_NAME);
        BigDecimal balance = result.get(TestProc.Out.BALANCE);

        assertThat(userName).isEqualTo("john_doe");
        assertThat(balance).isEqualTo(new BigDecimal("1250.50"));
    }

    @Test
    @DisplayName("Should throw exception when context is null")
    void shouldThrowWhenContextIsNull() {
        assertThatThrownBy(() -> testProc.call()
                .with(TestProc.In.USER_ID, 123L)
                .execute((J4dbContext) null))
                .isInstanceOf(NullPointerException.class);
    }
}
