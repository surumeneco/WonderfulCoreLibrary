package co.surumene.wcl.api.runtime;

import java.util.Set;

/** A consumer-local dirty/due queue indexed by a caller-chosen key, never by global Entity state.
 * The caller's clock determines deadline units. Operate on one owning thread.
 */
public interface WorkIndex<K> extends AutoCloseable {
    /** Marks a key for immediate attention, superseding any pending deadline. */
    void markDirty(K key);
    /** Replaces a key's existing deadline; units are defined by the owning consumer. */
    void schedule(K key, long due);
    /** Cancels both dirty and scheduled work for a key. */
    void discard(K key);
    /** Removes and returns the union of dirty and due work. */
    Set<K> takeReady(long now);
    /** Earliest deadline, or Long.MAX_VALUE if none; dirty work is represented by Long.MIN_VALUE. */
    long nextDeadline();
    /** Number of distinct outstanding keys. */
    int size();
    @Override void close();
}
