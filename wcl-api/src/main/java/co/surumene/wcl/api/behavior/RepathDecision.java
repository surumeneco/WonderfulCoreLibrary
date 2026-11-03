package co.surumene.wcl.api.behavior;

/** Species-independent decision only; a consumer owns the pathfinder and movement threshold. */
public final class RepathDecision {
    private RepathDecision() {}

    /** Preserve the original comparison semantics, including NaN displacement. */
    public static boolean needsRepath(
            boolean hasPath, boolean targetChanged, double displacementSquared,
            double thresholdSquared) {
        return !hasPath || targetChanged || displacementSquared >= thresholdSquared;
    }
}
