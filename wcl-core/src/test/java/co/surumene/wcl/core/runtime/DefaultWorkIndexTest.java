package co.surumene.wcl.core.runtime;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DefaultWorkIndexTest {
    @Test void deadlineAndDirtyHaveDistinctSemantics() {
        var index = new DefaultWorkIndex<String>();
        index.schedule("a", 40);
        index.schedule("b", 60);
        assertEquals(40, index.nextDeadline());
        assertTrue(index.takeReady(39).isEmpty());
        index.markDirty("b");
        assertEquals(Long.MIN_VALUE, index.nextDeadline());
        assertEquals(Set.of("b"), index.takeReady(39));
        assertEquals(Set.of("a"), index.takeReady(40));
        assertEquals(0, index.size());
    }
    @Test void rescheduleAndCancelPreventStaleDeadlines() {
        var index = new DefaultWorkIndex<String>();
        index.schedule("a", 40);
        index.schedule("a", 90);
        index.schedule("b", 50);
        index.discard("b");
        assertTrue(index.takeReady(50).isEmpty());
        assertEquals(Set.of("a"), index.takeReady(90));
    }
    @Test void closedIndexRejectsFurtherWork() {
        var index = new DefaultWorkIndex<String>();
        index.markDirty("a");
        index.close();
        index.close();
        assertThrows(IllegalStateException.class, () -> index.markDirty("b"));
        assertThrows(IllegalStateException.class, () -> index.takeReady(5));
    }
}
