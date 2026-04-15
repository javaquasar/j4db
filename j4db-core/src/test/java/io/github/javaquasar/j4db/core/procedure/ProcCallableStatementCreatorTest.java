package io.github.javaquasar.j4db.core.procedure;

import io.github.javaquasar.j4db.core.DatabaseDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcCallableStatementCreator Tests")
class ProcCallableStatementCreatorTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private CallableStatement mockCallableStatement;

    private TestProc testProc;

    private static class TestProc extends EnumBasedProc<TestProc.In, TestProc.Out> {

        public TestProc() {
            super("public.get_user_balance", new TestDialect());
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
            BALANCE("balance", java.sql.Types.NUMERIC, java.math.BigDecimal.class, ProcParamDirection.OUT);

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
        testProc = new TestProc();
    }

    @Test
    @DisplayName("Should create CallableStatement without throwing")
    void shouldCreateCallableStatement() throws Exception {
        when(mockConnection.prepareCall(anyString())).thenReturn(mockCallableStatement);

        ProcCallableStatementCreator creator = new ProcCallableStatementCreator(testProc, Map.of());

        CallableStatement cs = creator.createCallableStatement(mockConnection);

        assertThat(cs).isNotNull();
        verify(mockConnection).prepareCall(anyString());
    }

    @Test
    @DisplayName("Should not throw when input map is empty")
    void shouldNotThrowWithEmptyInputMap() throws Exception {
        when(mockConnection.prepareCall(anyString())).thenReturn(mockCallableStatement);

        ProcCallableStatementCreator creator = new ProcCallableStatementCreator(testProc, Map.of());

        assertThatNoException().isThrownBy(() -> creator.createCallableStatement(mockConnection));
    }
}
