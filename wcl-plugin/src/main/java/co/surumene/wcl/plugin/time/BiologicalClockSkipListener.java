package co.surumene.wcl.plugin.time;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ClockTimeSkipEvent;
import org.bukkit.event.world.TimeSkipEvent;

import java.util.Objects;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/** Owner-registered Paper listener: no global WCL clock or automatic registration. */
public class BiologicalClockSkipListener implements Listener {
    private final Supplier<String> worldName;
    private final LongConsumer recordIgnoredSkip;
    private final Logger logger;

    public BiologicalClockSkipListener(
            Supplier<String> worldName, LongConsumer recordIgnoredSkip, Logger logger) {
        this.worldName = Objects.requireNonNull(worldName, "worldName");
        this.recordIgnoredSkip = Objects.requireNonNull(recordIgnoredSkip, "recordIgnoredSkip");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTimeSkip(TimeSkipEvent event) {
        if (!event.getWorld().getName().equals(worldName.get())) return;

        ClockTimeSkipEvent.SkipReason reason = event.getSkipReason();
        if (reason == ClockTimeSkipEvent.SkipReason.COMMAND
                || reason == ClockTimeSkipEvent.SkipReason.CUSTOM) {
            try {
                recordIgnoredSkip.accept(event.getSkipAmount());
            } catch (RuntimeException error) {
                logger.warning("Could not persist ignored biological time skip; "
                        + "the clock basis was left unchanged: " + error.getMessage());
            }
        }
    }
}
