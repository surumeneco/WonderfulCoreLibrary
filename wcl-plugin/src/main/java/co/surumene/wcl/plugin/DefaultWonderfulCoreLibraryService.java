package co.surumene.wcl.plugin;

import co.surumene.wcl.api.runtime.ConsumerContext;
import co.surumene.wcl.core.runtime.DefaultConsumerContext;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/** Internal owner-to-context index. Never stores WWC/WWW Entity or configuration data. */
final class DefaultWonderfulCoreLibraryService implements WonderfulCoreLibraryService {
    private final Map<Plugin, Set<ConsumerContext>> contexts = new IdentityHashMap<>();
    private boolean closed;

    @Override public ConsumerContext openContext(Plugin owner) {
        requireMainThread();
        if (closed) throw new IllegalStateException("WCL service is closed");
        Objects.requireNonNull(owner, "owner");
        if (!owner.isEnabled()) throw new IllegalStateException("Consumer plugin is disabled");
        ConsumerContext context = new DefaultConsumerContext();
        contexts.computeIfAbsent(owner,
                ignored -> Collections.newSetFromMap(new IdentityHashMap<>())).add(context);
        return context;
    }

    void closeOwner(Plugin owner) {
        requireMainThread();
        Set<ConsumerContext> resources = contexts.remove(owner);
        if (resources != null) for (ConsumerContext resource : resources) resource.close();
    }

    void shutdown() {
        requireMainThread();
        if (closed) return;
        closed = true;
        for (Plugin owner : Set.copyOf(contexts.keySet())) closeOwner(owner);
    }

    private static void requireMainThread() {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException("WCL service must run on main thread");
    }
}
