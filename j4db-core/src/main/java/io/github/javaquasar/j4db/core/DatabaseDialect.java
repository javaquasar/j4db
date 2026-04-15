package io.github.javaquasar.j4db.core;

/**
 * Abstraction for database-specific behavior and type handling.
 * Each supported database (PostgreSQL, Oracle, MySQL, etc.) must provide its own implementation.
 *
 * <p>This is the main extension point for multi-database support.</p>
 *
 * @since 1.0.0
 */
public interface DatabaseDialect {

    /**
     * Returns a unique name of the database dialect.
     *
     * @return dialect name (e.g. "postgresql", "oracle")
     */
    String getName();

    /**
     * Returns the default schema name for this dialect if not specified.
     *
     * @return default schema or empty string
     */
    default String getDefaultSchema() {
        return "";
    }

    /**
     * Retrieves a value from ResultSet according to database-specific rules and type conversion.
     *
     * @param rs the ResultSet
     * @param columnName the column name
     * @param javaType the target Java type
     * @return converted value
     * @throws SQLException if a database access error occurs
     */
    Object getValue(java.sql.ResultSet rs, String columnName, Class<?> javaType) throws java.sql.SQLException;
}
