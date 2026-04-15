package io.github.javaquasar.j4db.dialect;

/**
 * Supported database types in J4DB.
 * Used by the generator and dialect registry.
 *
 * @since 1.0.0
 */
public enum DatabaseType {

    POSTGRESQL("postgresql"),
    ORACLE("oracle"),
    MYSQL("mysql"),
    MARIA_DB("mariadb"),
    SQL_SERVER("sqlserver");

    private final String name;

    DatabaseType(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the database type.
     */
    public String getName() {
        return name;
    }

    /**
     * Converts a string name to DatabaseType (case insensitive).
     *
     * @throws IllegalArgumentException if the name is not supported
     */
    public static DatabaseType fromName(String name) {
        for (DatabaseType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported database type: " + name);
    }
}
