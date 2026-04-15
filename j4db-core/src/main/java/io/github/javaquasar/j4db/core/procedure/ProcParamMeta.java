package io.github.javaquasar.j4db.core.procedure;

/**
 * Base metadata interface for a stored procedure parameter.
 *
 * <p>All IN and OUT parameters must implement this interface.</p>
 *
 * @since 1.0.0
 */
public interface ProcParamMeta {

    /**
     * Returns the name of the parameter as defined in the stored procedure.
     *
     * @return parameter name (never null)
     */
    String paramName();

    /**
     * Returns the JDBC type of this parameter.
     *
     * @return JDBC type code from {@link java.sql.Types}
     */
    int jdbcType();

    /**
     * Returns the recommended Java type for this parameter.
     *
     * @return Java class
     */
    Class<?> javaType();

    /**
     * Returns the direction of the parameter (IN, OUT, or IN_OUT).
     *
     * @return parameter direction
     */
    ProcParamDirection direction();

    /**
     * Optional native database type name (e.g. "jsonb", "TABLE", "REF CURSOR").
     */
    default String nativeTypeName() {
        return "";
    }
}
