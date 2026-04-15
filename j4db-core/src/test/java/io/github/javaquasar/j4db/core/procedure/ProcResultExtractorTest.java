package io.github.javaquasar.j4db.core.procedure;

import io.github.javaquasar.j4db.core.DatabaseDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.CallableStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcResultExtractor Tests")
class ProcResultExtractorTest {

    @Mock
    private CallableStatement mockCallableStatement;

    private TestProc testProc;
    private ProcResultExtractor<TestProc.Out> extractor;

    private static class TestProc extends EnumBasedProc<TestProc.In, TestProc.Out> {

        public TestProc() {
            super("public.test_proc", new TestDialect());
        }

        public enum In implements ProcIn {
            INPUT_ID("input_id", java.sql.Types.INTEGER, Integer.class, ProcParamDirection.IN);

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
            RESULT_VALUE("result_value", java.sql.Types.VARCHAR, String.class, ProcParamDirection.OUT);

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
        extractor = new ProcResultExtractor<>(testProc);
    }

    @Test
    @DisplayName("Should create extractor without error")
    void shouldCreateExtractor() {
        assertThat(extractor).isNotNull();
    }

    @Test
    @DisplayName("Should not throw when doInCallableStatement is called")
    void shouldNotThrowOnDoInCallableStatement() throws SQLException {
        assertThatNoException().isThrownBy(() -> extractor.doInCallableStatement(mockCallableStatement));
    }
}
