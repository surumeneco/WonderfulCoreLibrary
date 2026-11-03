package co.surumene.wcl.api.time;

/**
 * Pure shared evaluation of a validated normalized-rank age curve.
 * Consumer plugins own factor decoding, age-duration configuration and data validation.
 */
public final class AgeCurveMath {
    private AgeCurveMath() { }

    public static double rankReductionAt(double ageGameDays,
            double growthEndGameDay, double juvenileSuppressionRank,
            double agingStartGameDay, double elderGameDay, double maximumAgingReductionRank) {
        if (!Double.isFinite(ageGameDays)) {
            throw new IllegalArgumentException("ageGameDays must be finite");
        }
        double age = Math.max(0.0, ageGameDays);
        if (age < growthEndGameDay) {
            return growthEndGameDay == 0.0 ? 0.0
                    : juvenileSuppressionRank * (1.0 - smoothstep(age / growthEndGameDay));
        }
        if (age < agingStartGameDay) return 0.0;
        if (age < elderGameDay) {
            double duration = elderGameDay - agingStartGameDay;
            return duration == 0.0 ? maximumAgingReductionRank
                    : maximumAgingReductionRank * smoothstep((age - agingStartGameDay) / duration);
        }
        return maximumAgingReductionRank;
    }

    public static double smoothstep(double value) {
        double x = Math.max(0.0, Math.min(1.0, value));
        return 3.0 * x * x - 2.0 * x * x * x;
    }
}
