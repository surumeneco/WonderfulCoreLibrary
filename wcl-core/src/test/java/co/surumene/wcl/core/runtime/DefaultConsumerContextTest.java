package co.surumene.wcl.core.runtime;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DefaultConsumerContextTest {
    @Test void contextsNeverShareWork() {
        var first = new DefaultConsumerContext();
        var second = new DefaultConsumerContext();
        var a = first.<String>newWorkIndex();
        var b = second.<String>newWorkIndex();
        a.markDirty("shared-id");
        assertEquals(Set.of("shared-id"), a.takeReady(0));
        assertTrue(b.takeReady(0).isEmpty());
        first.close();
        assertThrows(IllegalStateException.class, () -> a.markDirty("x"));
        b.markDirty("still-working");
        assertEquals(Set.of("still-working"), b.takeReady(1));
        second.close();
    }
    @Test void membershipIsLocalAndStopsAfterOwnerClose() {
        var scope = new DefaultConsumerContext();
        var members = scope.<String>newActiveMembership();
        assertTrue(members.activate("wolf"));
        assertFalse(members.activate("wolf"));
        assertEquals(Set.of("wolf"), members.snapshot());
        scope.close();
        assertTrue(scope.isClosed());
        assertThrows(IllegalStateException.class, () -> members.activate("chicken"));
        assertThrows(IllegalStateException.class, scope::newWorkIndex);
    }
}
