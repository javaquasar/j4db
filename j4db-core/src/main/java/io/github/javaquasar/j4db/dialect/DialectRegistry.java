package io.github.javaquasar.j4db.dialect;

import io.github.javaquasar.j4db.core.DatabaseDialect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry for {@link DatabaseDialect} implementations.
 *
 * <p>Thread-safe, supports built-in lightweight dialects and allows overriding.
 *
 * @since 1.0.0
 */
public final class DialectRegistry {

    private static final Map<String, DatabaseDialect> REGISTRY = new ConcurrentHashMap<>();

    static {
        registerBuiltInDialects();
    }

    /**
     * Registers all built-in lightweight dialects.
     */
    private static void registerBuiltInDialects() {
        register(DatabaseType.POSTGRESQL, new PostgresDialect());
        // Add other built-in dialects here later (Oracle, MySQL, etc.)
    }

    /**
     * Registers or overrides a dialect for the given database type.
     */
    public static void register(DatabaseType type, DatabaseDialect dialect) {
        if (type == null) {
            throw new IllegalArgumentException("DatabaseType must not be null");
        }
        if (dialect == null) {
            throw new IllegalArgumentException("Dialect implementation must not be null");
        }
        register(type.getName(), dialect);
    }

    /**
     * Registers or overrides a dialect by its name (case-insensitive).
     */
    public static void register(String name, DatabaseDialect dialect) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dialect name must not be null or blank");
        }
        if (dialect == null) {
            throw new IllegalArgumentException("Dialect implementation must not be null");
        }
        REGISTRY.put(name.toLowerCase(), dialect);
    }

    /**
     * Returns the dialect for the given name (case-insensitive).
     */
    public static DatabaseDialect getDialect(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dialect name must not be null or blank");
        }

        DatabaseDialect dialect = REGISTRY.get(name.toLowerCase());
        if (dialect == null) {
            throw new IllegalArgumentException(
                    "Unsupported database type: '" + name + "'. " +
                            "Available dialects: " + String.join(", ", REGISTRY.keySet())
            );
        }
        return dialect;
    }

    /**
     * Returns the dialect for the given {@link DatabaseType}.
     */
    public static DatabaseDialect getDialect(DatabaseType type) {
        if (type == null) {
            throw new IllegalArgumentException("DatabaseType must not be null");
        }
        return getDialect(type.getName());
    }

    /**
     * Clears the registry and restores built-in dialects.
     * Intended for testing.
     */
    public static void clear() {
        REGISTRY.clear();
        registerBuiltInDialects();
    }

    /**
     * Checks if a dialect is registered for the given name.
     */
    public static boolean isRegistered(String name) {
        return name != null && REGISTRY.containsKey(name.toLowerCase());
    }

    /**
     * Returns current number of registered dialects.
     */
    public static int size() {
        return REGISTRY.size();
    }
}
