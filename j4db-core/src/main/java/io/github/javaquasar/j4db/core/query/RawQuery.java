package io.github.javaquasar.j4db.core.query;

import io.github.javaquasar.j4db.core.EnumBasedRowMapper;
import io.github.javaquasar.j4db.core.J4dbContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder for raw SQL queries with simple parameter binding.
 *
 * @param <T> the type of entity to map to
 * @since 1.0.0
 */
public final class RawQuery<T> {

    private final J4dbContext context;
    private final Class<? extends EnumBasedRowMapper<T, ?>> mapperClass;
    private String sql;
    private final List<Object> params = new ArrayList<>();

    public RawQuery(J4dbContext context, Class<? extends EnumBasedRowMapper<T, ?>> mapperClass) {
        this.context = Objects.requireNonNull(context);
        this.mapperClass = Objects.requireNonNull(mapperClass);
    }

    public RawQuery<T> sql(String sql) {
        this.sql = Objects.requireNonNull(sql, "SQL must not be null");
        return this;
    }

    public RawQuery<T> param(Object value) {
        params.add(value);
        return this;
    }

    public List<T> executeList() {
        validate();
        EnumBasedRowMapper<T, ?> mapper = instantiateMapper();
        return context.jdbcTemplate().query(sql, params.toArray(), mapper);
    }

    public T executeOne() {
        validate();
        EnumBasedRowMapper<T, ?> mapper = instantiateMapper();
        List<T> results = context.jdbcTemplate().query(sql, params.toArray(), mapper);
        return results.isEmpty() ? null : results.get(0);
    }

    public T executeOneOrThrow() {
        T result = executeOne();
        if (result == null) {
            throw new IllegalStateException("Expected exactly one result, but got none");
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
}
