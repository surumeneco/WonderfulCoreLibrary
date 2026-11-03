package co.surumene.wcl.api.ability;
/** Species-neutral eleven-rank index; caller chooses its already-observed invalid-number contract. */
public final class NormalizedRankIndex {
    private NormalizedRankIndex(){}
    public static int of(double normalized, boolean rejectNonfinite) {
        if(rejectNonfinite && !Double.isFinite(normalized))
            throw new IllegalArgumentException("normalized must be finite");
        if(normalized > 1.25) return 10;
        if(normalized > 1.0) return 9;
        double clamped=Math.max(0.0,Math.min(1.0,normalized));
        return Math.min(8,(int)Math.floor(clamped*9.0));
    }
}
