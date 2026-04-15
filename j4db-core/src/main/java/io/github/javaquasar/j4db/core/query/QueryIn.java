package io.github.javaquasar.j4db.core.query;

/**
 * Marker interface for type-safe query input parameters.
 *
 * <p>Used in the Typed Query API to provide compile-time safety for input parameters.</p>
 *
 * @since 1.0.0
 */
public interface QueryIn {

    /**
     * Returns the name of the parameter as it appears in the SQL query.
     *
     * @return parameter name
     */
    String paramName();

    /**
     * Optional position of the parameter when using positional placeholders (?).
     *
     * @return 1-based position or -1 if not applicable
     */
    default int position() {
        return -1;
    }
}
