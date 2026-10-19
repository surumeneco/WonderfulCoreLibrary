package co.surumene.wcl.api.runtime;

import java.util.Set;

/** Consumer-local set of work sources. Eligibility and scheduling remain consumer policies. */
public interface ActiveMembership<K> extends AutoCloseable {
    boolean activate(K key);
    boolean deactivate(K key);
    Set<K> snapshot();
    int size();
    @Override void close();
}
