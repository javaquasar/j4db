# J4DB — Type-Safe Spring JDBC Mapper & Column Enum Generator

![Java](https://img.shields.io/badge/Java-21%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3%2B-brightgreen)
![License](https://img.shields.io/badge/License-Apache%202.0-green)

**J4DB** is a modern, lightweight, and fully type-safe JDBC abstraction layer for Spring Boot applications.

It automatically generates **column enums** from your database tables and provides a clean, fluent API for both raw and fully compile-time safe queries, as well as type-safe stored procedure calls.

## ✨ Key Features

- Automatic generation of type-safe `XXXColumns` enums implementing `ColumnMeta`
- Two powerful query APIs:
    - `RawQuery` — simple and flexible
    - `TypedQuery` — fully compile-time safe using `QueryIn` enums
- Type-safe stored procedure support (`EnumBasedStoredProc`, `ProcIn` / `ProcOut`)
- Excellent **PostgreSQL** support (UUID, JSONB, arrays, INET, etc.)
- Multi-database support via `DatabaseDialect` + `DialectRegistry`
- First-class support for Java Records (immutable entities)
- Lightweight core with minimal dependencies
- Spring Boot Starter with auto-configuration
- Ready for code generator

## 📦 Project Modules

| Module                         | Artifact ID                          | Status        | Description |
|--------------------------------|--------------------------------------|---------------|-----------|
| **j4db-core**                  | `j4db-core`                          | ✅ Stable      | Core runtime library |
| **j4db-generator**             | `j4db-generator`                     | 🔄 In progress | Code generation engine |
| **j4db-spring-boot3-starter**  | `j4db-spring-boot3-starter`          | ✅ Ready       | **Main** starter for Spring Boot 3.x |
| **j4db-spring-boot4-starter**  | `j4db-spring-boot4-starter`          | 🔄 Planned    | Future starter for Spring Boot 4.x |

## 🚀 Quick Start

### 1. Add dependency (Maven)

```xml
<dependency>
    <groupId>io.github.javaquasar</groupId>
    <artifactId>j4db-spring-boot3-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Gradle

```groovy
implementation 'io.github.javaquasar:j4db-spring-boot3-starter:1.0.0-SNAPSHOT'
```

### 2. Configure in application.yml

```YAML
j4db:
  dialect: postgresql
  default-schema: public
```

### 3. Usage Example

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final J4dbContext j4db;

    public List<User> findActiveUsers() {
        return j4db.query(UserRowMapper.class)
                .sql("SELECT * FROM users WHERE is_active = ?")
                .param(true)
                .executeList();
    }

    public User findById(Long id) {
        return j4db.typedQuery(UserRowMapper.class)
                .sql("SELECT * FROM users WHERE id = :userId")
                .param(UserQueryParams.USER_ID, id)
                .executeOneOrThrow();
    }
}
```

# Philosophy

> **"Write SQL. Stay type-safe."**

J4DB sits between raw JDBC and heavy ORMs — giving you full control over SQL while eliminating boilerplate mapping and stringly-typed parameters.

 Roadmap

- [x] Core runtime and `EnumBasedRowMapper`
- [x] Raw & Typed Query API
- [x] Stored Procedure support
- [ ] Code generator (tables → Columns enum + Mapper)
- [ ] Full Spring Boot auto-configuration
- [ ] Gradle & Maven plugins

# License

This project is licensed under the Apache License 2.0 — see the LICENSE file for details.

---

Built with ❤️ for clean and maintainable Java backend development.
Questions, suggestions, or contributions are welcome! Feel free to open an issue or pull request.
