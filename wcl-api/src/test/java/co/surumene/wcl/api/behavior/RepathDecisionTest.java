package co.surumene.wcl.api.behavior;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RepathDecisionTest {
    @Test void keepsExistingPathBelowDisplacementThreshold() {
        assertFalse(RepathDecision.needsRepath(true, false, 0.249, 0.25));
        assertFalse(RepathDecision.needsRepath(true, false, Double.NaN, 0.25));
    }
    @Test void replansWhenLostChangedOrMovedToBoundary() {
        assertTrue(RepathDecision.needsRepath(false, false, 0.0, 0.25));
        assertTrue(RepathDecision.needsRepath(true, true, 0.0, 0.25));
        assertTrue(RepathDecision.needsRepath(true, false, 0.25, 0.25));
    }
}
