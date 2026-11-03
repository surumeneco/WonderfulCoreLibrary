package co.surumene.wcl.api.traits;

/** Source-independent potion refresh decision; callers retain effect ownership and timing. */
public final class PotionRefreshDecision {
    private PotionRefreshDecision() {}

    public static boolean shouldApply(int currentAmplifier, int currentDurationTicks,
                                      int desiredAmplifier, int refreshThresholdTicks) {
        // Paper uses -1 for infinite potion effects. Any other negative duration
        // is invalid; refresh thresholds are always finite nonnegative ticks.
        if (currentDurationTicks < -1 || refreshThresholdTicks < 0) {
            throw new IllegalArgumentException(
                    "duration must be -1 (infinite) or >= 0; refresh threshold must be >= 0");
        }
        return currentAmplifier < desiredAmplifier
                || (currentAmplifier == desiredAmplifier
                    && currentDurationTicks >= 0
                    && currentDurationTicks <= refreshThresholdTicks);
    }
}
