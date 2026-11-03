package co.surumene.wcl.core.runtime;

import co.surumene.wcl.api.runtime.ActiveMembership;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** Main-thread active work set. Consumer decides when/if to run a timer. */
public final class DefaultActiveMembership<K> implements ActiveMembership<K> {
    private final Set<K> active = new LinkedHashSet<>();
    private boolean closed;
    @Override public boolean activate(K key) {
        ensureOpen();
        return active.add(Objects.requireNonNull(key, "key"));
    }
    @Override public boolean deactivate(K key) {
        ensureOpen();
        return active.remove(Objects.requireNonNull(key, "key"));
    }
    @Override public Set<K> snapshot() {
        ensureOpen();
        return Set.copyOf(active);
    }
    @Override public int size() {
        ensureOpen();
        return active.size();
    }
    @Override public void close() {
        closed = true;
        active.clear();
    }
    private void ensureOpen() {
        if (closed) throw new IllegalStateException("ActiveMembership is closed");
    }
}
