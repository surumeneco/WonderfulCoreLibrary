package co.surumene.wcl.core.runtime;

import co.surumene.wcl.api.runtime.WorkIndex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/** Small, pure-Java proof-of-contract implementation. One owner thread only. */
public final class DefaultWorkIndex<K> implements WorkIndex<K> {
    private final Set<K> dirty = new LinkedHashSet<>();
    private final Map<K, Long> dueForKey = new HashMap<>();
    private final NavigableMap<Long, Set<K>> byDue = new TreeMap<>();
    private boolean closed;

    @Override public void markDirty(K key) {
        ensureOpen();
        Objects.requireNonNull(key, "key");
        unschedule(key);
        dirty.add(key);
    }

    @Override public void schedule(K key, long due) {
        ensureOpen();
        Objects.requireNonNull(key, "key");
        dirty.remove(key);
        unschedule(key);
        dueForKey.put(key, due);
        byDue.computeIfAbsent(due, ignored -> new HashSet<>()).add(key);
    }

    @Override public void discard(K key) {
        ensureOpen();
        Objects.requireNonNull(key, "key");
        dirty.remove(key);
        unschedule(key);
    }

    @Override public Set<K> takeReady(long now) {
        ensureOpen();
        Set<K> ready = new LinkedHashSet<>(dirty);
        dirty.clear();
        while (!byDue.isEmpty() && byDue.firstKey() <= now) {
            Map.Entry<Long, Set<K>> entry = byDue.pollFirstEntry();
            for (K key : entry.getValue()) {
                if (dueForKey.remove(key, entry.getKey())) ready.add(key);
            }
        }
        return Set.copyOf(ready);
    }

    @Override public long nextDeadline() {
        ensureOpen();
        if (!dirty.isEmpty()) return Long.MIN_VALUE;
        return byDue.isEmpty() ? Long.MAX_VALUE : byDue.firstKey();
    }

    @Override public int size() {
        ensureOpen();
        return dirty.size() + dueForKey.size();
    }

    @Override public void close() {
        if (closed) return;
        closed = true;
        dirty.clear();
        dueForKey.clear();
        byDue.clear();
    }

    private void unschedule(K key) {
        Long prev = dueForKey.remove(key);
        if (prev == null) return;
        Set<K> bucket = byDue.get(prev);
        if (bucket != null) {
            bucket.remove(key);
            if (bucket.isEmpty()) byDue.remove(prev);
        }
    }

    private void ensureOpen() {
        if (closed) throw new IllegalStateException("WorkIndex is closed");
    }
}
