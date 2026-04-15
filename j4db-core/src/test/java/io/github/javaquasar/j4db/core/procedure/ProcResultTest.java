package io.github.javaquasar.j4db.core.procedure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ProcResult Tests")
class ProcResultTest {

    private enum TestOutParams implements ProcOut {
        USER_NAME("user_name", java.sql.Types.VARCHAR, String.class, ProcParamDirection.OUT),
        BALANCE("balance", java.sql.Types.NUMERIC, BigDecimal.class, ProcParamDirection.OUT),
        IS_ACTIVE("is_active", java.sql.Types.BOOLEAN, Boolean.class, ProcParamDirection.OUT);

        private final String paramName;
        private final int jdbcType;
        private final Class<?> javaType;
        private final ProcParamDirection direction;

        TestOutParams(String paramName, int jdbcType, Class<?> javaType, ProcParamDirection direction) {
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

    @Test
    @DisplayName("Should return correct values for output parameters")
    void shouldReturnCorrectValues() {
        Map<String, Object> rawResult = Map.of(
                "user_name", "john_doe",
                "balance", new BigDecimal("1250.75"),
                "is_active", true
        );

        ProcResult<TestOutParams> result = new ProcResult<>(rawResult);

        String userName = result.get(TestOutParams.USER_NAME);
        BigDecimal balance = result.get(TestOutParams.BALANCE);
        Boolean isActive = result.get(TestOutParams.IS_ACTIVE);

        assertThat(userName).isEqualTo("john_doe");
        assertThat(balance).isEqualTo(new BigDecimal("1250.75"));
        assertThat(isActive).isTrue();
    }

    @Test
    @DisplayName("Should return null for missing or null parameters")
    void shouldReturnNullForMissingParameters() {
        // Use HashMap because Map.of() does not allow null values
        Map<String, Object> rawResult = new HashMap<>();
        rawResult.put("user_name", "john_doe");
        rawResult.put("balance", null);

        ProcResult<TestOutParams> result = new ProcResult<>(rawResult);

        assertThat((String) result.get(TestOutParams.USER_NAME)).isEqualTo("john_doe");
        assertThat((BigDecimal) result.get(TestOutParams.BALANCE)).isNull();
        assertThat((Boolean) result.get(TestOutParams.IS_ACTIVE)).isNull();
    }

    @Test
    @DisplayName("Should correctly check parameter presence with has()")
    void shouldCheckParameterPresence() {
        Map<String, Object> rawResult = Map.of(
                "user_name", "john_doe",
                "balance", new BigDecimal("500.00")
        );

        ProcResult<TestOutParams> result = new ProcResult<>(rawResult);

        assertThat(result.has(TestOutParams.USER_NAME)).isTrue();
        assertThat(result.has(TestOutParams.BALANCE)).isTrue();
        assertThat(result.has(TestOutParams.IS_ACTIVE)).isFalse();
    }

    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        Map<String, Object> mutableMap = new HashMap<>();
        mutableMap.put("user_name", "john_doe");

        ProcResult<TestOutParams> result = new ProcResult<>(mutableMap);

        mutableMap.put("user_name", "hacked");

        assertThat((String) result.get(TestOutParams.USER_NAME)).isEqualTo("john_doe");
    }
}
