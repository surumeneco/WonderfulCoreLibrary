package co.surumene.wcl.api.traits;

/** Source-independent potion refresh decision; callers retain effect ownership and timing. */
public final class PotionRefreshDecision {
    private PotionRefreshDecision() {}

    public static boolean shouldApply(int currentAmplifier, int currentDurationTicks,
                                      int desiredAmplifier, int refreshThresholdTicks) {
        if (currentDurationTicks < 0 || refreshThresholdTicks < 0) {
            throw new IllegalArgumentException("durations must be >= 0");
        }
        return currentAmplifier < desiredAmplifier
                || (currentAmplifier == desiredAmplifier
                    && currentDurationTicks <= refreshThresholdTicks);
    }
}
