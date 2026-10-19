# WonderfulCoreLibrary (WCL)

**Development scaffold — not a functional WWC/WWW replacement.**

Paper 26.2 / Java 25 library plugin for shared Minecraft runtime primitives used by
[WhatAWonderfulChicken](https://github.com/surumeneco/WhatAWonderfulChicken) and
[WhatAWonderfulWolf](https://github.com/surumeneco/WhatAWonderfulWolf).

The cross-plugin design source of truth is the [WCL responsibility and extraction proposal](https://drive.google.com/file/d/1c5rIsZN22smILI8VEhuxWB3j8ePPZE_B/view).

## Boundaries

- **No config.yml, lang, user data, or game balance defaults.** Each consumer owns its own configuration, PDC keys, data location and gameplay behavior.
- **No dependency on WGL.** The consumers will connect WCL and WGL independently. Genome synthesis, decoding and mutation remain entirely in WGL.
- **Per-consumer isolation.** The API exposes consumer-owned, closable contexts, never one shared cross-plugin entity index.
- **No automatic world scans or timers.** Consumers decide which entities to watch and when to schedule work.
- This scaffold **does not yet migrate any logic from WWC/WWW**. Only generic in-memory work indexes and active membership collections have working implementations.

## Modules

| Module | Role |
| --- | --- |
| wcl-api | Public Java/Paper-independent contracts |
| wcl-core | Pure-Java runtime primitives and scoped lifecycle |
| wcl-plugin | Paper bootstrap, ServicesManager service and owner-disable cleanup |

The **single deployable** plugin JAR is wcl-plugin/build/libs/WonderfulCoreLibrary-0.1.0-SNAPSHOT.jar (its jar bundles WCL API and core classes). Do **not** deploy the individual API/core JARs.

## Build

Require JDK 25 and Gradle 9.1+:

    gradle clean build --no-daemon

GitHub Actions builds on JDK 25 and verifies the single deployable JAR.

## Consumer API example (illustrative; migration not done)

    WonderfulCoreLibraryService service = Bukkit.getServicesManager()
            .load(WonderfulCoreLibraryService.class);
    if (service == null) throw new IllegalStateException("WCL service unavailable");

    ConsumerContext context = service.openContext(this);
    WorkIndex<UUID> refresh = context.newWorkIndex();
    refresh.markDirty(entityId);
    Set<UUID> changed = refresh.takeReady(serverTick);

The consumer itself validates entity identity, dispatches Paper operations, and closes its context.

After a future integration, WWC/WWW will declare depend: [WonderfulGenomeLib, WonderfulCoreLibrary] and compile against WCL. **Do not add that dependency before a functionality-preserving migration is ready.**

## Planned extraction (not implemented)

1. Biological clock and state-store adapters
2. External immutable Genome/Phenotype payload store preserving WGPF v1 bytes
3. Age and rank evaluation with explicit compatibility tests for existing edge cases
4. Bounded async founder dispatch and stale-entity validation
5. Config-value validation helpers and nearby-search/potion policies
6. Consumer-specific adapters for persistence, appearance, AI, traps, GUI and breeding

See [scaffold-status.md](docs/scaffold-status.md) for what exists and what is deliberately deferred.
