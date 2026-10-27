package co.surumene.wcl.api.config;

/** Pure predicates. Consumer-specific error messages, settings and defaults are not shared. */
public final class NumericValidation {
    private NumericValidation() {}
    public static boolean finite(double value) { return Double.isFinite(value); }
    public static boolean positive(double value) { return Double.isFinite(value) && value > 0.0; }
    public static boolean nonNegative(double value) { return Double.isFinite(value) && value >= 0.0; }
    public static boolean nonNegative(int value) { return value >= 0; }
    public static boolean unit(double value) {
        return Double.isFinite(value) && value >= 0.0 && value <= 1.0;
    }
    public static boolean ordered(double min, double max) { return min <= max; }
    public static boolean atLeastOne(int value) { return value >= 1; }
}
