package co.surumene.wcl.plugin.time;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ClockTimeSkipEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

class BiologicalClockSkipListenerTest {
    @Test void monitorOnlyIgnoringCancelledMatchesExistingListenerContract() throws Exception {
        Method handler = BiologicalClockSkipListener.class.getMethod("onTimeSkip", TimeSkipEvent.class);
        EventHandler annotation = handler.getAnnotation(EventHandler.class);
        assertNotNull(annotation);
        assertEquals(EventPriority.MONITOR, annotation.priority());
        assertTrue(annotation.ignoreCancelled());
    }

    @Test void onlyCommandAndCustomOnConfiguredWorldApplyOffset() {
        AtomicLong offset = new AtomicLong();
        AtomicReference<String> selected = new AtomicReference<>("world");
        var listener = new BiologicalClockSkipListener(selected::get, offset::addAndGet,
                Logger.getLogger("clock-test"));
        World included = Mockito.mock(World.class);
        Mockito.when(included.getName()).thenReturn("world");
        World excluded = Mockito.mock(World.class);
        Mockito.when(excluded.getName()).thenReturn("resource");

        dispatch(listener, included, ClockTimeSkipEvent.SkipReason.COMMAND, 123);
        dispatch(listener, included, ClockTimeSkipEvent.SkipReason.CUSTOM, 23);
        dispatch(listener, excluded, ClockTimeSkipEvent.SkipReason.COMMAND, 1000);
        for (ClockTimeSkipEvent.SkipReason reason : ClockTimeSkipEvent.SkipReason.values()) {
            if (reason != ClockTimeSkipEvent.SkipReason.COMMAND
                    && reason != ClockTimeSkipEvent.SkipReason.CUSTOM) {
                dispatch(listener, included, reason, 1000);
            }
        }
        assertEquals(146, offset.get());
        selected.set("resource");
        dispatch(listener, excluded, ClockTimeSkipEvent.SkipReason.COMMAND, 5);
        assertEquals(151, offset.get());
    }

    @Test void failureDoesNotThrowOrRetryAndLogsWarning() {
        AtomicLong calls = new AtomicLong();
        var logger = Logger.getLogger("clock-test-failure");
        var listener = new BiologicalClockSkipListener(() -> "world", amount -> {
            calls.incrementAndGet();
            throw new IllegalStateException("test failure");
        }, logger);
        World world = Mockito.mock(World.class);
        Mockito.when(world.getName()).thenReturn("world");
        assertDoesNotThrow(() -> dispatch(listener, world,
                ClockTimeSkipEvent.SkipReason.COMMAND, 5));
        assertEquals(1, calls.get());
    }

    private static void dispatch(BiologicalClockSkipListener listener, World world,
            ClockTimeSkipEvent.SkipReason reason, long amount) {
        TimeSkipEvent event = Mockito.mock(TimeSkipEvent.class);
        Mockito.when(event.getWorld()).thenReturn(world);
        Mockito.when(event.getSkipReason()).thenReturn(reason);
        Mockito.when(event.getSkipAmount()).thenReturn(amount);
        listener.onTimeSkip(event);
    }
}
