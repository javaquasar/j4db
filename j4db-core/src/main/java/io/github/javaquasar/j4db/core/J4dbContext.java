package io.github.javaquasar.j4db.core;

import io.github.javaquasar.j4db.core.query.QueryIn;
import io.github.javaquasar.j4db.core.query.RawQuery;
import io.github.javaquasar.j4db.core.query.TypedQuery;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Central facade and single entry point for all J4DB database operations.
 *
 * <p>This class encapsulates configuration (DataSource, Dialect, default schema)
 * and provides access to both Raw and Type-Safe query APIs as well as stored procedures.</p>
 *
 * @since 1.0.0
 */
public final class J4dbContext {

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseDialect dialect;
    private final String defaultSchema;

    private J4dbContext(Builder builder) {
        this.jdbcTemplate = Objects.requireNonNull(builder.jdbcTemplate, "jdbcTemplate must not be null");
        this.dialect = Objects.requireNonNull(builder.dialect, "dialect must not be null");
        this.defaultSchema = Objects.requireNonNullElse(builder.defaultSchema, "");
    }

    public static Builder builder() {
        return new Builder();
    }

    // ====================== Query APIs ======================

    /**
     * Starts building a raw SQL query (simple and flexible).
     */
    public <T> RawQuery<T> query(Class<? extends EnumBasedRowMapper<T, ?>> mapperClass) {
        return new RawQuery<>(this, mapperClass);
    }

    /**
     * Starts building a fully type-safe query using QueryParamIn enum.
     */
    public <T, P_IN extends QueryIn> TypedQuery<T, P_IN> typedQuery(
            Class<? extends EnumBasedRowMapper<T, ?>> mapperClass) {
        return new TypedQuery<>(this, mapperClass);
    }

    // ====================== Getters ======================

    public JdbcTemplate jdbcTemplate() {
        return jdbcTemplate;
    }

    public DatabaseDialect dialect() {
        return dialect;
    }

    public String defaultSchema() {
        return defaultSchema;
    }

    /**
     * Builder for {@link J4dbContext}.
     */
    public static final class Builder {

        private DataSource dataSource;
        private JdbcTemplate jdbcTemplate;
        private DatabaseDialect dialect;
        private String defaultSchema;

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder jdbcTemplate(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
            return this;
        }

        public Builder dialect(DatabaseDialect dialect) {
            this.dialect = dialect;
            return this;
        }

        public Builder defaultSchema(String defaultSchema) {
            this.defaultSchema = defaultSchema;
            return this;
        }

        public J4dbContext build() {
            if (jdbcTemplate == null) {
                if (dataSource == null) {
                    throw new IllegalStateException("Either dataSource or jdbcTemplate must be provided");
                }
                jdbcTemplate = new JdbcTemplate(dataSource);
            }
            return new J4dbContext(this);
        }
    }
}
