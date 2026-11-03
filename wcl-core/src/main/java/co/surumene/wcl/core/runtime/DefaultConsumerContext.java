package co.surumene.wcl.core.runtime;

import co.surumene.wcl.api.runtime.ActiveMembership;
import co.surumene.wcl.api.runtime.ConsumerContext;
import co.surumene.wcl.api.runtime.WorkIndex;
import java.util.ArrayList;
import java.util.List;

/** Only retains resources created by this context; no static/shared individual state. */
public final class DefaultConsumerContext implements ConsumerContext {
    private final List<AutoCloseable> resources = new ArrayList<>();
    private boolean closed;

    @Override public co.surumene.wcl.api.persistence.ImmutablePayloadStore newPayloadStore(java.nio.file.Path directory) {
        ensureOpen();
        return new co.surumene.wcl.core.persistence.WgpfRecordStore(directory);
    }

    @Override public <K> WorkIndex<K> newWorkIndex() {
        ensureOpen();
        WorkIndex<K> index = new DefaultWorkIndex<>();
        resources.add(index);
        return index;
    }

    @Override public <K> ActiveMembership<K> newActiveMembership() {
        ensureOpen();
        ActiveMembership<K> membership = new DefaultActiveMembership<>();
        resources.add(membership);
        return membership;
    }

    @Override public boolean isClosed() { return closed; }

    @Override public void close() {
        if (closed) return;
        closed = true;
        for (AutoCloseable resource : resources) {
            try { resource.close(); }
            catch (Exception ex) { throw new IllegalStateException("Could not close WCL resource", ex); }
        }
        resources.clear();
    }

    private void ensureOpen() {
        if (closed) throw new IllegalStateException("ConsumerContext is closed");
    }
}
