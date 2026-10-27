# WonderfulCoreLibrary (WCL)

Shared Paper/Java runtime primitives for [WhatAWonderfulChicken](https://github.com/surumeneco/WhatAWonderfulChicken) (WWC) and [WhatAWonderfulWolf](https://github.com/surumeneco/WhatAWonderfulWolf) (WWW).

**Release candidate only:** `develop` builds WCL `0.1.0` for integration checks alongside WWC `2.1.0` and WWW `1.1.0`. `main` is unchanged, and this is not a production release before Paper/JE/BE and performance acceptance. Successful CI does not replace Paper server/JE/BE testing or CPU/MSPT measurements.

The authoritative cross-plugin [responsibility and extraction design](https://drive.google.com/file/d/1c5rIsZN22smILI8VEhuxWB3j8ePPZE_B/view) specifies the compatibility and ownership boundaries. Validate implementation facts against the current `develop` source, not an earlier proposal's historical commit table.

## Boundaries

- **No WCL-owned config.yml, lang, gameplay defaults, Entity/PDC ownership, or consumer world data.** Each consumer supplies policy, keys, paths, clock, and runtime decisions.
- **WGL remains independent.** Genome synthesis, decoding, mutation, marker computation, and physical constraints are WGL's responsibility. WCL does not depend on WGL.
- **Per-consumer isolation.** `ConsumerContext` owns closable `WorkIndex` and `ActiveMembership` instances. Consumers retain scheduling, identity checks, worker executors, Paper-thread constraints, lifecycle and rollback responsibilities.
- **No automatic world scans, global tick tasks, or background job dispatch.** WCL helpers operate only when invoked by a consumer.
- Preserve saved formats and product-specific semantics. Extraction alone is not a performance optimization claim.

## Modules and distribution

| Module | Responsibility |
| --- | --- |
| `wcl-api` | Shared contracts and portable decision/queue/value helpers |
| `wcl-core` | Java runtime indexes, scoped lifecycle, and WGPF payload storage |
| `wcl-plugin` | Paper bootstrap, Bukkit service, nearby search, time-skip/YAML adapters and other Paper integration |

**Deploy only** `wcl-plugin/build/libs/WonderfulCoreLibrary-0.1.0.jar`. Its output bundles WCL API and core classes; do not deploy the API/core JARs separately. WWC/WWW compile against WCL without embedding its classes and declare the runtime plugin as a dependency.

## Implemented on `develop` (2026-10-27 verification)

- Consumer-local `WorkIndex`, `ActiveMembership`, and plugin lifecycle context.
- WGPF v1 external payload primitives; consumer-specific world paths and migration remain consumer-owned.
- SNBT-like parser and biological clock/state implementation.
- Paper time-skip listener adapter, biological-clock YAML store, and nearby-player query.
- Age curve and age-deadline math, normalized rank index; consumer-specific compatibility behavior remains explicit.
- Config-key path suggestions; **not** a shared config file or complete config validator.
- Read-only inventory storage snapshot/result contracts, with WWC and WWW provider adapters in their respective repositories.
- Consumer-owned `BoundedDispatchQueue`: FIFO bounded founder submissions, validity predicate when required, cancellation/removal and rejected-head restoration.

Each extraction has corresponding library tests, with consumer integration covered by WWC/WWW regression tests and CI. The above is **repository integration status**, not a claim of live-server verification. Version `0.1.0` is an unreleased validation candidate, not a published tag.

## Not yet fully extracted or proven

- Broader config numeric/value validators, potion conflict rules, navigation replan decisions, founder distributions, trait selection, and breeding math.
- Consumer-specific Entity/AI/PDC, inventory mutations, presentation, trap runtimes and full Genome workflows (not planned for blanket migration).
- Complete Paper enable/disable/reload regression, JE/BE field behavior, A/B CPU/MSPT and allocation comparisons, and production release.

These are potential work units and verification gates; see the design for A/B/C/D classification. Avoid changing gameplay rules or time/probability semantics merely to reduce duplicate code.

## Build and integration

JDK 25, Gradle 9.1+:

```sh
gradle clean build --no-daemon
```

GitHub Actions builds the three modules and checks the single deployable plugin artifact. Consumer CI checks out WGL and WCL at `develop` beside the consumer repository and uses Gradle composite builds; local consumer builds require the same sibling checkout layout.

To obtain WCL runtime services on Paper, a consumer can use Bukkit `ServicesManager`:

```java
WonderfulCoreLibraryService service =
        Bukkit.getServicesManager().load(WonderfulCoreLibraryService.class);
if (service == null) {
    throw new IllegalStateException("WCL service unavailable");
}
ConsumerContext context = service.openContext(this);
```

The owning consumer closes its context and validates main-thread/lifecycle access. WCL itself does not schedule individual jobs.

The original [2026-10-19 scaffold snapshot](docs/scaffold-status.md) is historical. It is not the current implementation checklist.
