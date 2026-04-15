package io.github.javaquasar.j4db.core;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Generic type-safe RowMapper that uses an enum implementing {@link ColumnMeta}
 * to map database result sets to Java entities.
 *
 * @param <T> the entity type
 * @param <E> the enum type that implements {@link ColumnMeta}
 *
 * @since 1.0.0
 */
public abstract class EnumBasedRowMapper<T, E extends Enum<E> & ColumnMeta>
        implements RowMapper<T> {

    protected final Class<T> entityClass;
    protected final Supplier<T> constructor;
    protected final Class<E> columnsEnumClass;

    protected EnumBasedRowMapper(Class<T> entityClass,
                                 Supplier<T> constructor,
                                 Class<E> columnsEnumClass) {
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass must not be null");
        this.constructor = Objects.requireNonNull(constructor, "constructor must not be null");
        this.columnsEnumClass = Objects.requireNonNull(columnsEnumClass, "columnsEnumClass must not be null");
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T entity = constructor.get();

        for (E column : columnsEnumClass.getEnumConstants()) {
            Object value = rs.getObject(column.columnName(), column.javaType());
            setValue(entity, column, value);
        }

        return entity;
    }

    /**
     * Sets the value of a column into the entity instance.
     * For immutable Records, override this to use constructor-based mapping.
     */
    protected abstract void setValue(T entity, E column, Object value);

    // Public getters for testing and introspection
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Class<E> getColumnsEnumClass() {
        return columnsEnumClass;
    }
}
