package io.github.javaquasar.j4db.dialect;

import io.github.javaquasar.j4db.core.DatabaseDialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

/**
 * PostgreSQL-specific implementation of {@link DatabaseDialect}.
 *
 * <p>Provides optimized value extraction for PostgreSQL native types:
 * <ul>
 *   <li>{@code UUID}</li>
 *   <li>{@code JSON} / {@code JSONB}</li>
 *   <li>Arrays (including multidimensional)</li>
 *   <li>{@code inet}, {@code cidr}, {@code macaddr}</li>
 *   <li>{@code hstore} (as Map)</li>
 * </ul>
 *
 * <p>Falls back to {@code ResultSet.getObject(columnName, javaType)} for unknown types.
 *
 * @since 1.0.0
 */
public final class PostgresDialect implements DatabaseDialect {

    private static final String NAME = "postgresql";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDefaultSchema() {
        return "public";
    }

    /**
     * Retrieves a value from the {@link ResultSet} with PostgreSQL-specific handling.
     *
     * <p>Special cases:
     * <ul>
     *   <li>{@code UUID} → {@link java.util.UUID}</li>
     *   <li>{@code JSON} / {@code JSONB} → {@link String} (or {@code JsonNode} if Jackson is on classpath — not used here to keep core lightweight)</li>
     *   <li>Array types → {@code Object[]} or primitive arrays when possible</li>
     *   <li>Network types ({@code inet}, {@code cidr}, {@code macaddr}) → {@link String}</li>
     * </ul>
     *
     * @param rs         the ResultSet
     * @param columnName the column name
     * @param javaType   the requested Java type
     * @return the value converted according to PostgreSQL rules
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Object getValue(ResultSet rs, String columnName, Class<?> javaType) throws SQLException {
        if (javaType == null) {
            return rs.getObject(columnName);
        }

        // UUID handling - PostgreSQL JDBC driver supports it natively
        if (javaType == UUID.class) {
            return rs.getObject(columnName, UUID.class);
        }

        // JSON / JSONB - return as String (most common and lightweight approach)
        // If you need structured JSON, add Jackson dependency in starter and extend this
        if (javaType == String.class) {
            int columnType = rs.getMetaData().getColumnType(rs.findColumn(columnName));
            if (columnType == Types.OTHER || columnType == Types.JAVA_OBJECT) {
                // Could be json/jsonb/hstore etc.
                String value = rs.getString(columnName);
                return value; // keep as JSON string
            }
        }

        // Array handling
        if (javaType.isArray()) {
            return rs.getArray(columnName) != null ? rs.getArray(columnName).getArray() : null;
        }

        // Network address types (inet, cidr, macaddr) → String
        if (javaType == String.class) {
            try {
                String typeName = rs.getMetaData().getColumnTypeName(rs.findColumn(columnName));
                if (typeName != null) {
                    String lowerType = typeName.toLowerCase();
                    if (lowerType.contains("inet") || lowerType.contains("cidr") || lowerType.contains("macaddr")) {
                        return rs.getString(columnName);
                    }
                }
            } catch (SQLException e) {
                // fallback if metadata is not available
            }
        }

        // hstore → Map<String, String> (if user requests Map)
        // For simplicity we return String; extend if needed with org.postgresql.util.HStore
        if (javaType == java.util.Map.class) {
            String typeName = rs.getMetaData().getColumnTypeName(rs.findColumn(columnName)).toLowerCase();
            if (typeName.contains("hstore")) {
                return rs.getObject(columnName); // PostgreSQL driver returns PGobject or Map in some versions
            }
        }

        // Default fallback - let JDBC driver handle standard types (LocalDateTime, BigDecimal, etc.)
        return rs.getObject(columnName, javaType);
    }
}
