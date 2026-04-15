package io.github.javaquasar.j4db.core.procedure;

import io.github.javaquasar.j4db.core.DatabaseDialect;

/**
 * Base class for type-safe stored procedure calls.
 *
 * <p>Use generics with {@link ProcIn} and {@link ProcOut} to achieve compile-time safety.</p>
 *
 * @param <IN>  enum type for input parameters (implements {@link ProcIn})
 * @param <OUT> enum type for output parameters (implements {@link ProcOut})
 *
 * @since 1.0.0
 */
public abstract class EnumBasedProc<IN extends Enum<IN> & ProcIn,
        OUT extends Enum<OUT> & ProcOut> {

    protected final String procedureName;
    protected final DatabaseDialect dialect;

    /**
     * Creates a new stored procedure wrapper.
     *
     * @param procedureName full procedure name (e.g. "public.get_user_by_id")
     * @param dialect       database dialect to use for type conversion
     */
    protected EnumBasedProc(String procedureName, DatabaseDialect dialect) {
        this.procedureName = procedureName;
        this.dialect = dialect;
    }

    /**
     * Starts a fluent call to this stored procedure.
     */
    public ProcCall<IN, OUT> call() {
        return new ProcCall<>(this);
    }

    public String getProcedureName() {
        return procedureName;
    }

    public DatabaseDialect getDialect() {
        return dialect;
    }

    /**
     * Registers all OUT parameters on the CallableStatement.
     * Usually implemented by the generated subclass.
     */
    protected abstract void registerOutParameters(java.sql.CallableStatement cs) throws java.sql.SQLException;
}
