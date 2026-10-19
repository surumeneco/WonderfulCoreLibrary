package co.surumene.wcl.api.runtime;

/** One consumer-owned lifecycle scope. No config, Entity references or world scanners. */
public interface ConsumerContext extends AutoCloseable {
    <K> WorkIndex<K> newWorkIndex();
    <K> ActiveMembership<K> newActiveMembership();
    boolean isClosed();
    @Override void close();
}
