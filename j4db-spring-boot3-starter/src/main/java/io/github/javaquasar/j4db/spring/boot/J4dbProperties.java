package io.github.javaquasar.j4db.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for J4DB.
 *
 * <p>Example in application.yml:
 * <pre>
 * j4db:
 *   dialect: postgresql
 *   default-schema: public
 * </pre>
 *
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "j4db")
public class J4dbProperties {

    /** Database dialect name (e.g. "postgresql", "oracle"). Default: postgresql */
    private String dialect = "postgresql";

    /** Default schema name used if not specified in queries */
    private String defaultSchema;

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }
}
