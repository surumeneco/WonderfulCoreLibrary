package co.surumene.wcl.api.genetics;

/** Common arithmetic only: consumers decide what 'strong' means and set probabilities. */
public final class BreedingMath {
    private BreedingMath() {}

    public static double directRetention(boolean strong,
                                         double weakProbability, double strongProbability) {
        return strong ? strongProbability : weakProbability;
    }

    public static double parentTraitMultiplier(boolean strong,
                                               double weakMultiplier, double strongMultiplier) {
        return strong ? strongMultiplier : weakMultiplier;
    }
}
