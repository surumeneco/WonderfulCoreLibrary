package co.surumene.wcl.api.genetics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;

/**
 * Consumer-parameterized founder target primitives. Does not know species, traits, WGL,
 * a probability distribution's config source, or an RNG implementation.
 * The rejection and draw sequence intentionally match WWC/WWW originals.
 */
public final class FounderSampling {
    private FounderSampling() {}

    public record TraitChoice<T>(T trait, boolean strong) {
        public TraitChoice { Objects.requireNonNull(trait, "trait"); }
    }

    public static double truncatedNormal(double mean, double standardDeviation,
                                         double min, double max, DoubleSupplier nextDouble) {
        Objects.requireNonNull(nextDouble, "nextDouble");
        for (;;) {
            double u1 = nextDouble.getAsDouble();
            double u2 = nextDouble.getAsDouble();
            if (!(u1 > 0.0)) continue;
            double z = StrictMath.sqrt(-2.0 * StrictMath.log(u1))
                    * StrictMath.cos(2.0 * StrictMath.PI * u2);
            double value = mean + standardDeviation * z;
            if (value >= min && value <= max) return value;
        }
    }

    /** Exactly two nextInt draws even when either result means "no trait". */
    public static <T> List<TraitChoice<T>> drawTraits(
            List<T> traits, ToIntFunction<? super T> targetId, IntUnaryOperator nextInt) {
        Objects.requireNonNull(traits, "traits");
        Objects.requireNonNull(targetId, "targetId");
        Objects.requireNonNull(nextInt, "nextInt");
        int none = traits.size();
        int first = nextInt.applyAsInt(none + 1);
        int second = nextInt.applyAsInt(none + 1);
        if (first == none && second == none) return List.of();
        if (first == second) return List.of(new TraitChoice<>(traits.get(first), true));
        List<TraitChoice<T>> out = new ArrayList<>(2);
        if (first != none) out.add(new TraitChoice<>(traits.get(first), false));
        if (second != none) out.add(new TraitChoice<>(traits.get(second), false));
        out.sort(Comparator.comparingInt(choice -> targetId.applyAsInt(choice.trait())));
        return List.copyOf(out);
    }
}
