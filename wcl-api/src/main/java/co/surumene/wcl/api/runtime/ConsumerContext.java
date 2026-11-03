package co.surumene.wcl.api.runtime;

/** One consumer-owned lifecycle scope. No config, Entity references or world scanners. */
public interface ConsumerContext extends AutoCloseable {
    co.surumene.wcl.api.persistence.ImmutablePayloadStore newPayloadStore(java.nio.file.Path directory);
    <K> WorkIndex<K> newWorkIndex();
    <K> ActiveMembership<K> newActiveMembership();
    boolean isClosed();
    @Override void close();
}
