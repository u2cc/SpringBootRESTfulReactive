# Spring Boot 4.x Migration Report

**Report date:** 2026-10-16  
**Skill used:** `.github/skills/spring-boot-upgrade/SKILL.md`  
**Scope:** Repository-specific analysis and migration plan. No application-code migration is performed by this report.

**Target selected:** Spring Boot **4.1.1**, the latest stable Boot 4 release listed in Maven Central metadata checked for this report. Boot 4.2 releases were milestone builds, not stable releases, at the time checked.

## A. Executive summary

This is a single-module Maven application using Spring WebFlux, Spring Security, Spring Data R2DBC, and a MySQL R2DBC driver. The POM declares Boot **3.1.2**. Its major migration work is a framework/dependency generation upgrade, validating third-party integrations, resolving direct dependency overrides, and adding regression tests; the current repository has no `src/test` source tree.

The current `Role` source has already moved away from JPA: it uses Spring Data Relational `@Table` and `@Transient` annotations, and the `users` field is assembled/ignored rather than persisted through JPA. No `javax.persistence` imports, JPA repositories, or Hibernate API references were found in current source. The explicitly pinned `org.hibernate:hibernate-core:5.6.7.Final` therefore appears unused and should be removed, after confirming there is no external runtime reliance.

Boot 4.1.1 requires Java **17–26** and Maven **3.6.3+**. The project’s IntelliJ configuration selects Java 17; the environment previously reported Java 27, outside the Boot 4.1.1 supported range. There is no Maven wrapper and Maven is not installed in the current environment, so the effective dependency tree, compilation, tests, and startup have not been verified.

The highest remaining third-party risk is `jasypt-spring-boot`: the project uses it to decrypt properties, but its published documentation found during this review describes Java 17+ and Boot 3.5+ and does not explicitly confirm Boot 4.1.1. Upgrade must be gated on proving encrypted-property behavior using the candidate release. The R2DBC MySQL driver and springdoc both have published Boot 4-compatible version evidence.

**Feasibility:** Reasonable, subject to Jasypt compatibility and integration testing against the configured MySQL/TLS environment. Keep public API routes, JSON shapes, login behavior, and database semantics stable.

## B. Dependency migration matrix

The current parent is Boot 3.1.2. Where the POM omits a version, the table reports the Boot 3.1.2 BOM-managed value as the expected version; the effective dependency tree could not be run. Target values are from the Boot 4.1.1 BOM. This repository has no additional Maven modules, parent POMs, BOM imports, or `dependencyManagement` section.

| Component | Current exact version | Target exact version | Action | Reason | Compatibility evidence | Confidence |
|---|---|---|---|---|---|---|
| `org.springframework.boot:spring-boot-starter-parent` | `3.1.2` | `4.1.1` | UPGRADE | Selected stable Boot 4 target. | [Maven Central parent metadata](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/maven-metadata.xml); [Boot 4.1.1 BOM](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/4.1.1/spring-boot-dependencies-4.1.1.pom). | High |
| `org.springframework.boot:spring-boot-starter-webflux` | Boot-managed `3.1.2` | Boot-managed `4.1.1` | UPGRADE | Retain reactive HTTP stack. Recheck controller streaming and serialization behavior. | Boot 4.1.1 parent/BOM and [Boot starter catalog](https://docs.spring.io/spring-boot/reference/using/build-systems.html). | High |
| `org.springframework.boot:spring-boot-starter-test` (test scope) | Boot-managed `3.1.2` | Boot-managed `4.1.1` | KEEP / UPGRADE | Keep framework test support; add tests because none exist. | Boot BOM/starter catalog. Boot 4.1.1 manages JUnit Jupiter `6.0.3`; effective test graph needs a build. | High |
| `io.projectreactor:reactor-test` (test scope) | Expected `3.5.8` from Reactor BOM `2022.0.9` | Boot-managed by Reactor BOM `2025.0.7` | KEEP / UPGRADE | Useful for testing reactive publishers. | Boot 3.1.2 and 4.1.1 BOMs; target BOM lists Reactor `2025.0.7`. | High for management; medium until resolved tree |
| `org.hibernate:hibernate-core` | Explicit `5.6.7.Final` | No replacement in recommended R2DBC-only design | REMOVE, confirm no external use | No current JPA/Hibernate source usage; `Role` is R2DBC-mapped. It is an old version override and can introduce unwanted ORM/Jakarta classes. | POM direct dependency; current source search shows no `org.hibernate` or `javax.persistence` use. | High |
| `org.projectlombok:lombok` | Explicit `1.18.24` (Boot 3.1.2 BOM would manage `1.18.28`) | Boot-managed `1.18.46` | UPGRADE; remove explicit version | Use the version tested/managed by the target Boot BOM instead of retaining an older override. | Boot 3.1.2 and Boot 4.1.1 BOMs. | High |
| `org.springframework.boot:spring-boot-starter-security` | Boot-managed `3.1.2` (Spring Security `6.1.2`) | Boot-managed `4.1.1` (Spring Security `7.1.1`) | UPGRADE | Required for reactive authentication APIs. Verify existing behavior; no confirmed source-level API replacement identified. | Boot BOMs; `SecurityConfig` uses `SecurityWebFilterChain` and `ServerHttpSecurity`. | High for versions; medium for runtime behavior |
| `io.r2dbc:r2dbc-spi` | Expected `1.0.0.RELEASE` via Boot 3.1.2 BOM | Boot-managed `1.0.0.RELEASE` | REMOVE if tree confirms starter supplies it | No direct SPI classes are referenced in source; R2DBC starter normally brings the SPI transitively. | POM and source scan; dependency tree unavailable. | Medium |
| `org.springframework.boot:spring-boot-starter-data-r2dbc` | Boot-managed `3.1.2` | Boot-managed `4.1.1` | UPGRADE | Core persistence stack; custom SQL/repository mapping requires integration checks. | Boot BOM/starter catalog. | High |
| `io.asyncer:r2dbc-mysql` | Explicit `1.0.2` (same as Boot 3.1.2’s managed driver property) | Boot-managed `1.4.3` | UPGRADE; remove explicit version/property | Driver project says Boot 4 support starts at 1.4.2; target Boot BOM supplies 1.4.3. | [Driver compatibility table](https://github.com/asyncer-io/r2dbc-mysql); target BOM. | High |
| `com.github.ulisesbocchio:jasypt-spring-boot` | Explicit `3.0.2` | `4.0.4` candidate; Boot 4.1 compatibility unverified | INVESTIGATE | `@EnableEncryptableProperties` and `ENC(...)` properties make this runtime-critical. The upstream README states Java 17+ / Boot 3.5+ rather than explicitly promising Boot 4. | [Jasypt project README](https://github.com/ulisesbocchio/jasypt-spring-boot); [Maven Central metadata](https://repo.maven.apache.org/maven2/com/github/ulisesbocchio/jasypt-spring-boot/maven-metadata.xml). | Medium for candidate; low for Boot 4.1 compatibility |
| `org.springdoc:springdoc-openapi-starter-webflux-ui` | Explicit `2.1.0` | `3.1.1` | UPGRADE | Current module version is old; springdoc 3.1.1 documentation advertises Boot 4 runtime support. | [springdoc docs](https://springdoc.org/); [artifact metadata](https://repo.maven.apache.org/maven2/org/springdoc/springdoc-openapi-starter-webflux-ui/maven-metadata.xml). | High |
| `org.springframework.boot:spring-boot-maven-plugin` | Inherited from Boot parent `3.1.2` | Inherited from Boot parent `4.1.1` | UPGRADE with parent | Keep packaging plugin aligned with Boot parent. | POM build section; no explicit plugin version override. | High |
| Maven Compiler Plugin | No project-level declaration; target parent manages plugin | Boot 4.1.1 BOM property `3.15.0` (effective plugin config not resolved) | KEEP / VERIFY | Configure Java release explicitly; avoid pinning plugin without need. | Target BOM and Boot [system requirements](https://docs.spring.io/spring-boot/system-requirements.html). | Medium |

**Other target BOM versions:** Boot 4.1.1 manages Spring Framework **7.0.9**, Spring Security **7.1.1**, Spring Data BOM **2026.0.1**, Reactor BOM **2025.0.7**, R2DBC MySQL **1.4.3**, Hibernate **7.4.5.Final**, and Lombok **1.18.46**. These are BOM values, not a resolved dependency tree for this app.

## C. Required dependency replacements

| Existing coordinates | Replacement coordinates | Old version | Proposed version | Reason | Impacted files |
|---|---|---:|---:|---|---|
| `org.hibernate:hibernate-core` | None for the current R2DBC-only design | `5.6.7.Final` | Remove | Current source uses Spring Data R2DBC mappings and custom R2DBC SQL; no JPA/Hibernate API usage is present. | `pom.xml`; no Hibernate source references found |
| `io.r2dbc:r2dbc-spi` | None; rely on `spring-boot-starter-data-r2dbc` if confirmed transitively supplied | Boot-managed, expected `1.0.0.RELEASE` | Remove only after dependency-tree confirmation | No direct SPI type use found. | `pom.xml`; R2DBC repository classes |

No mandatory new dependency replacement is confirmed. Update coordinates in place for the MySQL driver, springdoc, and (if verified) Jasypt. The latter remains a compatibility gate, not a confirmed replacement.

## D. Deprecated API replacement matrix

The current application source was searched for `javax.*`, `jakarta.*`, deprecated Spring APIs, old servlet security configuration, and JPA APIs. No `javax` imports or confirmed deprecated/removed Spring APIs were found. Do not apply a blanket namespace rewrite.

| File/symbol | Existing API | Replacement API | Required code change | Evidence | Testing requirement |
|---|---|---|---|---|---|
| `src/main/java/com/reactive/entities/Role.java` (`@Table`, `@Transient`) | Spring Data Relational annotations; `users` is transient and ignored for JSON | No replacement indicated | No namespace change. Keep `@Transient` semantics and `@JsonIgnore` unless the response contract is intentionally changed. | Current file imports Spring Data Relational mapping; project-wide source scan finds no `javax.persistence`. | R2DBC user/role query and JSON response regression tests. |
| `src/main/java/com/reactive/security/SecurityConfig.java` (`SecurityWebFilterChain`, `authorizeExchange`) | Reactive Spring Security chain | No replacement confirmed from current source/evidence | Recompile against Security 7.1.1 and preserve policy. The current chain disables CORS/CSRF and permits `/**`; this is existing behavior, not a migration recommendation. | Current SecurityConfig; no `WebSecurityConfigurerAdapter` use found. | Assert route access, login success/failure, authentication context, and response contract. |
| `src/main/java/com/reactive/repository/CustomUserRepositoryImpl.java` (`DatabaseClient`, named binds) | Spring R2DBC `DatabaseClient` | No replacement established | No planned code change unless the Boot 4 compile or official upgrade guidance requires one; preserve parameter binding. | Current R2DBC repository source; no incompatible usage established. | MySQL integration tests for user lookup, roles join, timestamps, no-match behavior. |
| `src/main/java/com/reactive/controller/SpringBootRestfulReactiveController.java` | WebFlux annotations and `Flux`/`Mono` handlers | No replacement established | Recompile and compare payloads/media types; retain `TEXT_EVENT_STREAM` endpoint behavior. | Current source uses WebFlux controller APIs. | `WebTestClient` tests for streaming, JSON, status codes, and errors. |
| `src/main/java/com/reactive/entities/User.java` and `Role.java` (Jackson annotations) | `com.fasterxml.jackson.annotation` annotations | Verify Boot 4 JSON integration; no replacement claimed | Ensure annotation library remains available through the chosen Boot 4 JSON starter arrangement and inspect serialized output. | Boot 4 dependency management changes the JSON stack; this project has no custom mapper inspected. | Compare representative JSON fields, dates, ignored fields, and OpenAPI schemas. |

No meaningful source-level replacement is currently confirmed, so there is no safe mechanical BEFORE/AFTER source diff to prescribe. The concrete POM change for the unused Hibernate dependency is shown in section G.

## E. Configuration migration matrix

Configuration was inspected in `src/main/resources/application.properties`. No property rename/removal is confirmed from the evidence gathered. Use Boot’s migrator temporarily at runtime if diagnostics are needed, then remove it.

| File/key | Old configuration | New configuration | Reason | Impact |
|---|---|---|---|---|
| `springdoc.api-docs.path`, `springdoc.swagger-ui.path`, `springdoc.swagger-ui.operationsSorter` | Custom OpenAPI path `/api-docs`, custom Swagger UI path, sorter `method` | No confirmed key change; preserve and verify under springdoc 3.1.1 | Preserve public docs paths and behavior across springdoc major update. | Check OpenAPI JSON and Swagger UI route. |
| `spring.r2dbc.url`, `spring.r2dbc.username`, password property | Local MySQL R2DBC configuration | No confirmed key change | Driver and framework versions change; connection semantics must remain. | Verify connectivity, URL options, server timezone, credentials, and mapping. |
| `server.ssl.*` | PKCS12 classpath keystore; TLS enabled | No confirmed key change | Preserve HTTPS behavior. | Start with keystore and validate HTTPS; keep passwords out of report/logs. |
| `jasypt.encryptor.*`, `jwt.secret` | Encrypted property integration | No new key proposed pending Jasypt verification | Jasypt dependency and decryption behavior are the unresolved external integration. | Verify property decryption with environment-provided key. |
| `logging.level.*`, `logging.pattern.console` | Custom levels and console pattern | No confirmed change | No active removed property identified. `logging.file` example is commented out. | Verify startup logging and MDC formatting. |
| Temporary Boot properties migrator | Not present | Optional `org.springframework.boot:spring-boot-properties-migrator` with runtime scope during migration only | Boot documents diagnostics and temporary migration for renamed/removed properties. | Read startup messages and remove dependency after all properties are corrected. |

No profile-specific config files were found. Secret values are deliberately not repeated here.

## F. Version-by-version upgrade sequence

1. **Baseline current Boot 3.1.2**
   - Make Maven 3.6.3+ available; use Java 17 (already configured in IntelliJ and within Boot 4’s supported range).
   - Capture current build and representative route responses, streaming media type, OpenAPI paths, login behavior, MySQL mapping, audit writes, and TLS startup.
   - Add tests before changing dependencies because no `src/test` source files exist.
   - Run and archive `dependency:tree -Dverbose`.
   - **Gate:** baseline compile/startup state and external prerequisites are documented.

2. **Upgrade to Boot 3.5.16**
   - This was the latest stable 3.5.x parent listed by Maven Central metadata checked for this report.
   - Remove Hibernate if external use is ruled out; remove unnecessary direct version overrides and resolve any deprecations surfaced by compiler/log output.
   - Test Jasypt on this line first, since upstream README states Boot 3.5+ support.
   - **Gate:** clean compile, tests pass, startup and integration tests work on Boot 3.5.16.

3. **Upgrade to Boot 4.0.8**
   - This was the latest stable 4.0.x parent in metadata. Boot 4.2 was only at milestone status at report time.
   - Upgrade the parent/plugin, review Boot 4 migration guidance for starter modularization and JSON changes, and compile/fix only evidence-backed breaks.
   - Upgrade springdoc to 3.1.1; move MySQL driver to Boot-managed 1.4.3; prove Jasypt compatibility before allowing encrypted config to gate app startup.
   - **Gate:** clean build and regression suite; app startup with MySQL, encrypted properties, and TLS.

4. **Upgrade to Boot 4.1.1**
   - Move one feature release forward from 4.0.8, review its release/upgrading notes, and use its managed Framework 7 / Security 7 / Data / Reactor versions.
   - **Gate:** full acceptance criteria below and no unintended dependency overrides/conflicts.

5. **Rollback**
   - Keep each migration stage in a separate branch or commit; do not mix schema changes with framework changes.
   - Preserve the last known-good build artifact and environment config.
   - If a stage fails, revert that stage’s application/POM changes together and restore its known-good artifact. No database migration is proposed by this plan.

## G. Exact proposed build-file changes

### Confirmed parent and Java settings

Boot 4.1.1 is stable and requires Java 17–26. The IDE project is set to Java 17, so use that runtime/compiler level unless production has a separately supported target:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.1</version>
    <relativePath/>
</parent>

<properties>
    <java.version>17</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

The `spring-boot-maven-plugin` has no explicit version; keep it parent-managed.

### Recommended dependency edits

- Keep Boot-managed WebFlux, Security, R2DBC, and test starters.
- Remove direct `org.hibernate:hibernate-core:5.6.7.Final`; current source has no JPA/Hibernate usage.
- Remove the explicit Lombok version and let the Boot BOM manage it.
- Remove `io.r2dbc:r2dbc-spi` only after the resolved tree confirms it is transitively supplied.
- Remove the `mysql-driver.version` property and explicit driver version; Boot 4.1.1 manages R2DBC MySQL `1.4.3`, a release documented as Boot 4 compatible.
- Change springdoc WebFlux UI from `2.1.0` to `3.1.1`.
- Keep Jasypt dependency in place until `4.0.4` or another candidate passes a Boot 4.1.1 integration test. Do not claim the version is compatible without evidence.
- Optionally add Boot’s properties migrator at runtime temporarily; remove it before finalizing.

Hibernate POM removal, if no external code depends on it:

```diff
-        <!-- https://mvnrepository.com/artifact/org.hibernate/hibernate-core -->
-        <dependency>
-            <groupId>org.hibernate</groupId>
-            <artifactId>hibernate-core</artifactId>
-            <version>5.6.7.Final</version>
-        </dependency>
```

MySQL driver and springdoc version changes:

```diff
-        <mysql-driver.version>1.0.2</mysql-driver.version>
...
         <dependency>
             <groupId>io.asyncer</groupId>
             <artifactId>r2dbc-mysql</artifactId>
-            <version>${mysql-driver.version}</version>
         </dependency>
...
         <dependency>
             <groupId>org.springdoc</groupId>
             <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
-            <version>2.1.0</version>
+            <version>3.1.1</version>
         </dependency>
```

The `3.1.1` springdoc value is the checked artifact release, not a Boot-managed dependency. Driver version omission relies on Boot 4.1.1’s BOM.

## H. Risk and testing matrix

| Area | Project-specific risk / evidence | Required verification |
|---|---|---|
| Compilation/build | No Maven wrapper or executable; Java 27 was previously reported in the environment but is outside Boot 4.1.1’s declared maximum 26. IDE compiler config says JDK 17. | Build with Maven 3.6.3+ and a supported JDK; add Maven Wrapper for reproducibility; use clean compile at each stage. |
| Dependency graph | Hibernate and Lombok are explicitly pinned; MySQL driver has a version property; Jasypt and springdoc are pinned. | Run `./mvnw dependency:tree -Dverbose`; inspect Spring/Jakarta/Hibernate/Reactor/R2DBC duplicate or mixed versions. |
| Unit tests | No test tree exists. | Add tests for service success/error behavior, exception mapping, user details, role mapping, and auditing. |
| Startup | Runtime requires MySQL, encryption key/config, and classpath TLS keystore. | Start under target Boot with deployment-like properties; validate full context initialization and shutdown. |
| HTTP/API | Controller has streaming, JSON, request parameter defaults, and custom OpenAPI paths. | Use `WebTestClient` to assert routes, status/body/media types, default parameters, streaming events, error payloads, and OpenAPI endpoints. |
| Database behavior | Custom `DatabaseClient` SQL loads user then roles; repositories include an `IN (:brands)` query; auditing uses date/time fields; service writes a car plus audit record transactionally. | Test MySQL SQL binding (including brand list edge cases), role joins, empty user result, date/time conversions, audit rows, and transaction rollback. |
| Security | Current reactive chain disables CORS/CSRF and permits all paths; controller has a login endpoint that authenticates explicitly. | Preserve current policy unless separately changed. Test permitted routes, login success/failure, password encoder, and security context handling. |
| Jasypt/secrets | Encrypted property values and `@EnableEncryptableProperties` are used; Boot 4.1 support is not explicitly established by upstream documentation found. | Test property decryption with the actual deployment mechanism; no secret values in output; treat failure as a blocker. |
| JSON/OpenAPI | springdoc jumps from 2.x to 3.1.1; Boot 4 dependency set changes JSON stack. | Compare representative DTO/entity JSON and schemas, ignored fields, temporal values, operation sorting, UI, and docs URL. |
| TLS/logging | TLS uses a packaged PKCS12 keystore; custom console pattern uses MDC. | Verify TLS handshake/certificate behavior and logging pattern at startup/runtime. |
| Messaging/cache/actuator | No messaging, cache, tracing, or Actuator source/dependency/config was found. | No dedicated migration work unless infrastructure outside this repository adds these integrations. |
| Deployment | No Dockerfile, CI workflow, or deployment manifests found. | Verify deployment JDK range, Maven build image, keystore packaging, and configuration injection before rollout. |

Acceptance requires a clean build, passing tests, no unintended dependency conflicts, successful startup in the supported runtime, and preservation of observed API/security/database behavior.

## I. Unresolved questions and blockers

1. **Is Hibernate used by any external module or runtime integration?** No use is visible in this single-module repository; remove only after confirming none exists outside the checked-in project.
2. **Which Java runtime is used in deployment?** Boot 4.1.1 supports Java 17–26; the IDE selects Java 17, but the prior environment reported Java 27.
3. **Can Maven and MySQL test infrastructure be made available?** There is no wrapper, no Maven executable, and no tests; dependency resolution and runtime compatibility remain unverified.
4. **Does Jasypt 4.0.4 work with Boot 4.1.1?** Maven Central lists 4.0.4, but the upstream documentation found says Boot 3.5+ and does not explicitly confirm Boot 4.1.1. Verify via targeted build/startup/decryption test.
5. **What MySQL server version and schema are deployed?** Needed to validate the 1.4.3 driver, SQL behavior, temporal mappings, and transaction semantics.
6. **Are there out-of-repository CI/deployment settings or internal dependencies?** No CI workflows or deployment manifests were found. These could add constraints not visible here.
7. **What is the required public contract for the current login and security policy?** The source currently permits all routes and returns login results directly; regression tests should preserve observed behavior, not infer a desired policy.

## Sources

- [Spring Boot 4.1.1 system requirements](https://docs.spring.io/spring-boot/system-requirements.html) — Java 17–26, Maven 3.6.3+, Framework 7.0.9.
- [Spring Boot 4.1.1 dependency BOM](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/4.1.1/spring-boot-dependencies-4.1.1.pom) — managed dependency versions.
- [Spring Boot upgrading guidance](https://docs.spring.io/spring-boot/upgrading.html) — temporary properties migrator.
- [Spring Boot starter catalog](https://docs.spring.io/spring-boot/reference/using/build-systems.html).
- [Spring Boot parent metadata](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/maven-metadata.xml) — stable 3.5.x, 4.0.x, 4.1.x releases vs milestone 4.2.
- [R2DBC MySQL compatibility table](https://github.com/asyncer-io/r2dbc-mysql).
- [springdoc documentation](https://springdoc.org/) and [artifact metadata](https://repo.maven.apache.org/maven2/org/springdoc/springdoc-openapi-starter-webflux-ui/maven-metadata.xml).
- [Jasypt Spring Boot README](https://github.com/ulisesbocchio/jasypt-spring-boot) and [artifact metadata](https://repo.maven.apache.org/maven2/com/github/ulisesbocchio/jasypt-spring-boot/maven-metadata.xml).
