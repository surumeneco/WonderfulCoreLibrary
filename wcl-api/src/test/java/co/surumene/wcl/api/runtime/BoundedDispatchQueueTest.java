package co.surumene.wcl.api.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundedDispatchQueueTest {
    @Test
    void doesNotPollCapacityOnAnEmptyQueue() {
        BoundedDispatchQueue<String> queue = new BoundedDispatchQueue<>();
        assertEquals(0, queue.drain(() -> {
            fail("idle queue must not query executor capacity");
            return 1;
        }, job -> fail("idle queue must not submit")));
    }

    @Test
    void retainsFifoOrderAndHonorsDynamicCapacity() {
        BoundedDispatchQueue<String> queue = new BoundedDispatchQueue<>();
        queue.offer("first");
        queue.offer("second");
        queue.offer("third");
        List<String> accepted = new ArrayList<>();
        int[] capacity = {1};
        assertEquals(1, queue.drain(() -> capacity[0], job -> {
            accepted.add(job);
            capacity[0]--;
        }));
        assertEquals(2, queue.size());
        capacity[0] = 2;
        assertEquals(2, queue.drain(() -> capacity[0], job -> {
            accepted.add(job);
            capacity[0]--;
        }));
        assertEquals(List.of("first", "second", "third"), accepted);
        assertTrue(queue.isEmpty());
    }

    @Test
    void returnsRejectedHeadToFrontForLaterWakeup() {
        BoundedDispatchQueue<String> queue = new BoundedDispatchQueue<>();
        queue.offer("first");
        queue.offer("second");
        assertEquals(0, queue.drain(() -> 1, job -> {
            throw new RejectedExecutionException("full");
        }));
        List<String> accepted = new ArrayList<>();
        assertEquals(2, queue.drain(() -> 1, accepted::add));
        assertEquals(List.of("first", "second"), accepted);
    }

    @Test
    void filtersInvalidJobsWithoutStarvingValidJobs() {
        BoundedDispatchQueue<String> queue = new BoundedDispatchQueue<>();
        queue.offer("stale");
        queue.offer("valid");
        List<String> accepted = new ArrayList<>();
        assertEquals(1, queue.drain("valid"::equals, () -> 1, accepted::add));
        assertEquals(List.of("valid"), accepted);
        assertTrue(queue.isEmpty());
    }

    @Test
    void allowsRemovalAndClearWithoutDraining() {
        BoundedDispatchQueue<String> queue = new BoundedDispatchQueue<>();
        queue.offer("first");
        queue.offer("second");
        assertTrue(queue.remove("first"));
        assertFalse(queue.remove("first"));
        assertEquals(1, queue.size());
        queue.clear();
        assertTrue(queue.isEmpty());
    }

    @Test
    void preventsNullJobsAndRejectsNullCallbacks() {
        BoundedDispatchQueue<String> queue = new BoundedDispatchQueue<>();
        assertThrows(NullPointerException.class, () -> queue.offer(null));
        assertThrows(NullPointerException.class, () -> queue.drain(null, job -> {}));
        assertThrows(NullPointerException.class, () -> queue.drain(() -> 1, null));
    }
}
