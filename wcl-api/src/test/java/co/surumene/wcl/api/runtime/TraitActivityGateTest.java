package co.surumene.wcl.api.runtime;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class TraitActivityGateTest {
    @Test void wwcsLastSourceStopsTickAndClearsAlerts() {
        assertFalse(TraitActivityGate.shouldRun(0, false));
        assertTrue(TraitActivityGate.shouldRun(1, false));
        assertTrue(TraitActivityGate.shouldClearAlerts(0));
        assertFalse(TraitActivityGate.shouldClearAlerts(1));
    }

    @Test void wwwHolyPulseKeepsSchedulerAliveWithoutRecurringSources() {
        assertTrue(TraitActivityGate.shouldRun(0, true));
        assertFalse(TraitActivityGate.shouldStop(0, true));
        assertTrue(TraitActivityGate.shouldStop(0, false));
        assertFalse(TraitActivityGate.shouldStop(8, false));
    }
}
