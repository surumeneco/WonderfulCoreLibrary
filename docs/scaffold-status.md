# WCL scaffold status (2026-10-19)

## Implemented in this prototype

- Three Gradle modules (wcl-api, wcl-core, wcl-plugin) on Java 25 / Paper 26.2.
- Deployable WCL Paper plugin publishes WonderfulCoreLibraryService via Bukkit ServicesManager.
- Per-owner ConsumerContext, with automatic cleanup on owner plugin disable and WCL disable.
- Consumer-local WorkIndex<K>: dirty, replaceable deadlines, cancellation, due extraction.
- Consumer-local ActiveMembership<K>: active-source membership with snapshots.
- JUnit contract tests and GitHub Actions build/JAR layout validation.
- No config.yml, no WGL runtime dependency, no game-specific immutable state, no automatic scheduled jobs.

## Not implemented; placeholders or future adapters only

- BiologicalClock and YAML clock-state compatibility
- WGPF v1 immutable GenomePayloadFiles persistence
- AgeCurve, rank classification, trait effect decisions, nearby search
- Founder queues, genome parser, pedigree, breeding, trap helpers
- Entity/PDC game object registration and restoration
- Async executor integration and Paper scheduling adapters
- WWC 2.1.0 / WWW 1.1.0 integration; their current code and config remain unchanged

## Design constraints for next phase

- API is a **provisional prototype**, not a frozen compatibility guarantee.
- Contract drift is possible: time tick sources are consumer-defined; never merge server-tick and biological-tick deadlines into the same WorkIndex unless the unit contract is identical.
- A single key denotes at most one dirty/deadline job **within one WorkIndex instance**. Use distinct indexes for different job domains (e.g. age and affection).
- No shared Entity references, global tick task or background executor in the WCL service.
- The caller must validate UUID/Entity generation, world-thread ownership, cancellations, permission and Paper state before consuming due work.
- Consumers own their settings, immutable runtime policy, persistence paths, errors, translation, and any timing/cpu budgets.
- Review existing WWC/WWW A/B baselines before replacing their dispatch loops. These primitives alone do not demonstrate performance improvement.

## Test list

1. Dirty work is ready once; deadline stays pending until its time.
2. Replacing/canceling a deadline does not fire the stale deadline.
3. Closing an index/context rejects future use; closes all resources.
4. Separate contexts do not share same-ID jobs or memberships.
5. Plugin metadata expands version and bundles three modules but no config.
6. Real Paper 26.2 enable/disable/service discovery and owner shutdown — **not yet tested on server**.
7. Runtime JAR used from independent consumer with compileOnly — **not yet tested**.

## Builds and release

The version 0.1.0-SNAPSHOT is prototype-only. Do not deploy alongside production consumers as if it already supplies biological clocks or other game features. Feature branches and CI checks must precede develop merge; no release or main update is implied.
