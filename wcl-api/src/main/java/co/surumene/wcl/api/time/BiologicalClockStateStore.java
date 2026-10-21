package co.surumene.wcl.api.time;

public interface BiologicalClockStateStore {
    BiologicalClockState load();
    void save(BiologicalClockState state);
}
