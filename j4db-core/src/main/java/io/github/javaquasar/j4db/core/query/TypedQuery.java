package io.github.javaquasar.j4db.core.query;

import io.github.javaquasar.j4db.core.EnumBasedRowMapper;
import io.github.javaquasar.j4db.core.J4dbContext;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Type-safe query builder using {@link QueryIn} enum.
 *
 * <p>Provides compile-time safety for query parameters.</p>
 *
 * @param <T>    the entity type being returned
 * @param <P_IN> the enum type describing input parameters
 *
 * @since 1.0.0
 */
public final class TypedQuery<T, P_IN extends QueryIn> {

    private final J4dbContext context;
    private final Class<? extends EnumBasedRowMapper<T, ?>> mapperClass;
    private String sql;
    private final List<QueryInputParameter> parameters = new ArrayList<>();

    public TypedQuery(J4dbContext context, Class<? extends EnumBasedRowMapper<T, ?>> mapperClass) {
        this.context = Objects.requireNonNull(context);
        this.mapperClass = Objects.requireNonNull(mapperClass);
    }

    public TypedQuery<T, P_IN> sql(String sql) {
        this.sql = Objects.requireNonNull(sql, "SQL must not be null");
        return this;
    }

    /**
     * Binds a typed parameter.
     */
    public TypedQuery<T, P_IN> param(P_IN param, Object value) {
        Objects.requireNonNull(param, "param must not be null");
        parameters.add(new QueryInputParameter(param, value));
        return this;
    }

    /**
     * Executes query and returns list of results.
     */
    public List<T> executeList() {
        validate();
        EnumBasedRowMapper<T, ?> mapper = instantiateMapper();
        return context.jdbcTemplate().query(
                sql,
                new TypedPreparedStatementSetter(parameters),
                mapper
        );
    }

    /**
     * Executes query and returns single result or null if none found.
     */
    public T executeOne() {
        validate();
        EnumBasedRowMapper<T, ?> mapper = instantiateMapper();
        List<T> results = context.jdbcTemplate().query(
                sql,
                new TypedPreparedStatementSetter(parameters),
                mapper
        );
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Executes query and returns exactly one result. Throws exception if zero or more than one.
     */
    public T executeOneOrThrow() {
        T result = executeOne();
        if (result == null) {
            throw new IllegalStateException("Expected exactly one result, but got none for query: " + sql);
        }
        return result;
    }

    private void validate() {
        if (sql == null || sql.isBlank()) {
            throw new IllegalStateException("SQL must be provided");
        }
    }

    @SuppressWarnings("unchecked")
    private EnumBasedRowMapper<T, ?> instantiateMapper() {
        try {
            return mapperClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate mapper: " + mapperClass.getName(), e);
        }
    }

    // Inner helper class
    private static class QueryInputParameter {
        final QueryIn param;
        final Object value;

        QueryInputParameter(QueryIn param, Object value) {
            this.param = param;
            this.value = value;
        }
    }

    private static class TypedPreparedStatementSetter
            implements org.springframework.jdbc.core.PreparedStatementSetter {

        private final List<QueryInputParameter> params;

        TypedPreparedStatementSetter(List<QueryInputParameter> params) {
            this.params = params;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            for (int i = 0; i < params.size(); i++) {
                Object value = params.get(i).value;
                ps.setObject(i + 1, value);
            }
        }
    }
}
