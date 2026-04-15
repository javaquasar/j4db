package io.github.javaquasar.j4db.core.procedure;

import io.github.javaquasar.j4db.core.J4dbContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Fluent builder for calling a stored procedure with type-safe parameters.
 *
 * @param <IN>  input parameter enum
 * @param <OUT> output parameter enum
 *
 * @since 1.0.0
 */
public final class ProcCall<IN extends Enum<IN> & ProcIn,
        OUT extends Enum<OUT> & ProcOut> {

    private final EnumBasedProc<IN, OUT> procedure;
    private final Map<IN, Object> inputValues = new HashMap<>();

    ProcCall(EnumBasedProc<IN, OUT> procedure) {
        this.procedure = Objects.requireNonNull(procedure);
    }

    /**
     * Binds an input parameter.
     */
    public ProcCall<IN, OUT> with(IN param, Object value) {
        Objects.requireNonNull(param, "param must not be null");
        inputValues.put(param, value);
        return this;
    }

    /**
     * Executes the stored procedure using J4dbContext.
     */
    public ProcResult<OUT> execute(J4dbContext context) {
        Objects.requireNonNull(context, "context must not be null");
        return context.jdbcTemplate().execute(
                new ProcCallableStatementCreator(procedure, inputValues),
                new ProcResultExtractor<>(procedure)
        );
    }

    /**
     * Executes the stored procedure using JdbcTemplate directly.
     */
    public ProcResult<OUT> execute(JdbcTemplate jdbcTemplate) {
        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
        return jdbcTemplate.execute(
                new ProcCallableStatementCreator(procedure, inputValues),
                new ProcResultExtractor<>(procedure)
        );
    }
}
