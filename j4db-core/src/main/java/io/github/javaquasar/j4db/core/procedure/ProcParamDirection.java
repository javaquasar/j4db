package io.github.javaquasar.j4db.core.procedure;

/**
 * Direction of a stored procedure parameter.
 *
 * @since 1.0.0
 */
public enum ProcParamDirection {

    IN("IN"),
    OUT("OUT"),
    IN_OUT("IN_OUT");

    private final String sqlName;

    ProcParamDirection(String sqlName) {
        this.sqlName = sqlName;
    }

    public String sqlName() {
        return sqlName;
    }
}
