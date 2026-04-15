package io.github.javaquasar.j4db.core.procedure;

import org.springframework.jdbc.core.CallableStatementCreator;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Creates and configures CallableStatement for stored procedure execution.
 *
 * @since 1.0.0
 */
class ProcCallableStatementCreator implements CallableStatementCreator {

    private final EnumBasedProc<?, ?> procedure;
    private final Map<?, Object> inputValues;

    ProcCallableStatementCreator(EnumBasedProc<?, ?> procedure, Map<?, Object> inputValues) {
        this.procedure = procedure;
        this.inputValues = Map.copyOf(inputValues);
    }

    @Override
    public CallableStatement createCallableStatement(Connection con) throws SQLException {
        String callString = buildCallString();
        CallableStatement cs = con.prepareCall(callString);

        int index = 1;

        // Bind IN parameters
        for (ProcIn inParam : getInParameters()) {
            Object value = inputValues.get(inParam);
            if (value == null) {
                cs.setNull(index, inParam.jdbcType());
            } else {
                cs.setObject(index, value, inParam.jdbcType());
            }
            index++;
        }

        // Register OUT parameters
        procedure.registerOutParameters(cs);

        return cs;
    }

    private String buildCallString() {
        // Simplified version - in real generator this should be more sophisticated
        return "{call " + procedure.getProcedureName() + "(?)}";
    }

    @SuppressWarnings("unchecked")
    private ProcIn[] getInParameters() {
        Class<?> cls = procedure.getClass().getEnclosingClass();
        if (cls != null && ProcIn.class.isAssignableFrom(cls)) {
            return (ProcIn[]) cls.getEnumConstants();
        }
        return new ProcIn[0];
    }
}
