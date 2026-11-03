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

    @Test void indefiniteDurationPreservesEqualAndStrongerEffects() {
        // Bukkit PotionEffect#getDuration uses -1 for an infinite effect.
        assertFalse(PotionRefreshDecision.shouldApply(1, -1, 1, 40));
        assertFalse(PotionRefreshDecision.shouldApply(2, -1, 1, 40));
        // An upgrade to a stronger level still follows the existing rule.
        assertTrue(PotionRefreshDecision.shouldApply(0, -1, 1, 40));
    }

    @Test void finiteDurationKeepsTheExistingInclusiveThreshold() {
        assertFalse(PotionRefreshDecision.shouldApply(1, 41, 1, 40));
        assertTrue(PotionRefreshDecision.shouldApply(1, 40, 1, 40));
        assertTrue(PotionRefreshDecision.shouldApply(1, 39, 1, 40));
    }

    @Test void rejectsInvalidDurationsButAcceptsTheInfiniteSentinel() {
        assertThrows(IllegalArgumentException.class,
                () -> PotionRefreshDecision.shouldApply(1, -2, 1, 40));
        assertThrows(IllegalArgumentException.class,
                () -> PotionRefreshDecision.shouldApply(1, 1, 1, -1));
    }
}
