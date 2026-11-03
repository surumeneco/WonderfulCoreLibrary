package co.surumene.wcl.api.genetics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BreedingMathTest {
    @Test void consumerSuppliesSpeciesSpecificStrongRetention() {
        assertEquals(1.0, BreedingMath.directRetention(true, 0.75, 1.0));
        assertEquals(0.75, BreedingMath.directRetention(false, 0.75, 1.0));
        double wolfStrong = Math.min(1.0, 0.3 * 2.0);
        assertEquals(0.6, BreedingMath.directRetention(true, 0.3, wolfStrong));
        assertEquals(0.3, BreedingMath.directRetention(false, 0.3, wolfStrong));
    }
    @Test void parentTraitMultiplierPreservesStrongAndWeakValues() {
        assertEquals(2.0, BreedingMath.parentTraitMultiplier(false, 2.0, 4.0));
        assertEquals(4.0, BreedingMath.parentTraitMultiplier(true, 2.0, 4.0));
    }
}
