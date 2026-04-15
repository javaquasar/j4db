package io.github.javaquasar.j4db.core;

/**
 * Represents metadata for a database column.
 * Used to provide type-safe mapping between database columns and Java entities.
 *
 * @since 1.0.0
 */
public interface ColumnMeta {

    /**
     * Returns the exact name of the column in the database.
     *
     * @return column name (never null)
     */
    String columnName();

    /**
     * Returns the JDBC type code from {@link java.sql.Types}.
     *
     * @return JDBC SQL type
     */
    int jdbcType();

    /**
     * Returns the recommended Java type for mapping this column.
     *
     * @return Java class for {@code ResultSet.getObject(String, Class)}
     */
    Class<?> javaType();

    /**
     * Optional human-readable label or comment for the column.
     *
     * @return column label or empty string if not available
     */
    default String label() {
        return "";
    }

    /**
     * Indicates whether this column allows null values.
     *
     * @return true if the column is nullable
     */
    default boolean nullable() {
        return true;
    }
}
