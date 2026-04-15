package io.github.javaquasar.j4db.core.procedure;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the result of a stored procedure execution.
 * Provides type-safe access to OUT parameters.
 *
 * @param <OUT> the enum type describing output parameters
 *
 * @since 1.0.0
 */
public final class ProcResult<OUT extends Enum<OUT> & ProcOut> {

    private final Map<String, Object> values;

    ProcResult(Map<String, Object> values) {
        this.values = new HashMap<>(values); // Map.of
    }

    /**
     * Returns the value of an output parameter.
     *
     * @param param the output parameter
     * @param <T>   the expected Java type
     * @return the value or null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(OUT param) {
        Object value = values.get(param.paramName());
        return value != null ? (T) value : null;
    }

    /**
     * Checks if a specific output parameter was returned.
     */
    public boolean has(OUT param) {
        return values.containsKey(param.paramName());
    }
}
