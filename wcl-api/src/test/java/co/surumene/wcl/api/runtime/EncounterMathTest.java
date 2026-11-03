package co.surumene.wcl.api.runtime;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EncounterMathTest {
    @Test void timeSkipDoesNotCountAsNaturalArrival() {
        assertTrue(EncounterMath.naturalArrival(17999L, 18000L, 24000L, 18000L));
        assertFalse(EncounterMath.naturalArrival(100L, 18000L, 24000L, 18000L));
        assertFalse(EncounterMath.naturalArrival(18000L, 18000L, 24000L, 18000L));
        assertTrue(EncounterMath.naturalArrival(41999L, 42000L, 24000L, 18000L));
    }
    @Test void probabilityAndDiskOffsetFollowExistingTraps() {
        assertFalse(EncounterMath.roll(0.5, 0.5));
        assertTrue(EncounterMath.roll(0.0, 1.0));
        var offset = EncounterMath.diskOffset(1.0, 0.25, 32.0);
        assertEquals(0.0, offset.x(), 1e-12);
        assertEquals(32.0, offset.z(), 1e-12);
    }
}
