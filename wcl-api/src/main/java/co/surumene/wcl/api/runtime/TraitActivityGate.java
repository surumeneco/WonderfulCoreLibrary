package co.surumene.wcl.api.runtime;

/** Pure activity gate; consumer owns task scheduling, cancellation and per-species conditions. */
public final class TraitActivityGate {
    private TraitActivityGate() {}

    /** Additional transient activity (e.g. HolyPulse) is caller-defined. */
    public static boolean shouldRun(int recurringSources, boolean transientActivity) {
        return recurringSources > 0 || transientActivity;
    }

    public static boolean shouldStop(int recurringSources, boolean transientActivity) {
        return !shouldRun(recurringSources, transientActivity);
    }

    /** WWC resets alert bookkeeping when its last recurring source disappears. */
    public static boolean shouldClearAlerts(int recurringSources) {
        return recurringSources == 0;
    }
}
