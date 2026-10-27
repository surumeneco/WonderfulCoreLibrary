package co.surumene.wcl.api.traits;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PotionRefreshDecisionTest {
    @Test void preservesEffectsFromOtherMechanics() {
        assertFalse(PotionRefreshDecision.shouldApply(2, 100, 1, 40));
        assertFalse(PotionRefreshDecision.shouldApply(1, 41, 1, 40));
        assertTrue(PotionRefreshDecision.shouldApply(1, 40, 1, 40));
        assertTrue(PotionRefreshDecision.shouldApply(0, 100, 1, 40));
    }
    @Test void validatesDurationsLikeWWW() {
        assertThrows(IllegalArgumentException.class,
                () -> PotionRefreshDecision.shouldApply(1, -1, 1, 40));
        assertThrows(IllegalArgumentException.class,
                () -> PotionRefreshDecision.shouldApply(1, 1, 1, -1));
    }
}
