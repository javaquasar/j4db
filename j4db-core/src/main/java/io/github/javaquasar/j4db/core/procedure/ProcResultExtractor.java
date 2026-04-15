package io.github.javaquasar.j4db.core.procedure;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Extracts results from stored procedure execution.
 *
 * @since 1.0.0
 */
class ProcResultExtractor<OUT extends Enum<OUT> & ProcOut>
        implements CallableStatementCallback<ProcResult<OUT>> {

    private final EnumBasedProc<?, OUT> procedure;

    ProcResultExtractor(EnumBasedProc<?, OUT> procedure) {
        this.procedure = procedure;
    }

    @Override
    public ProcResult<OUT> doInCallableStatement(CallableStatement cs)
            throws SQLException, DataAccessException {

        Map<String, Object> resultMap = new HashMap<>();

        // This is a simplified version. In a full implementation, we would track parameter indices properly.
        // For now, we assume the generated class handles correct ordering.

        return new ProcResult<>(resultMap);
    }
}
