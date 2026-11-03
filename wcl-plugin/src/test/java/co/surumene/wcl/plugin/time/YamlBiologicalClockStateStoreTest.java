package co.surumene.wcl.plugin.time;

import co.surumene.wcl.api.time.BiologicalClockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

final class YamlBiologicalClockStateStoreTest {
    @TempDir Path root;
    private final Logger logger = Logger.getLogger("wcl-yaml-test");

    @Test void roundTripPreservesExistingWorldOffsetAndLastTimeKeys() {
        Path file = root.resolve("nested").resolve("clock.yml");
        var store = new YamlBiologicalClockStateStore(file.toFile(), logger);
        assertNull(store.load());
        var expected = new BiologicalClockState("resource", -500L, 200_000L);
        store.save(expected);
        var yaml = YamlConfiguration.loadConfiguration(file.toFile());
        assertEquals("resource", yaml.getString("clock-world"));
        assertEquals(-500L, yaml.getLong("ignored-skip-offset"));
        assertEquals(200_000L, yaml.getLong("last-biological-time"));
        assertEquals(expected, store.load());
    }

    @Test void corruptOrIncompleteLegacyYamlReturnsNullRatherThanRebasingClock() throws Exception {
        Path file = root.resolve("invalid.yml");
        var store = new YamlBiologicalClockStateStore(file.toFile(), logger);
        java.nio.file.Files.writeString(file, "ignored-skip-offset: 3\nlast-biological-time: 20\n");
        assertNull(store.load());
        java.nio.file.Files.writeString(file, "clock-world: world\nignored-skip-offset: bad\nlast-biological-time: 20\n");
        assertNull(store.load());
        java.nio.file.Files.writeString(file, "clock-world: world\nignored-skip-offset: 2\nlast-biological-time: -1\n");
        assertNull(store.load());
    }

    @Test void writesIntoNestedFolderWithoutDefiningAnyFixedPluginPath() {
        Path file = root.resolve("owner-data").resolve("clock.yml");
        new YamlBiologicalClockStateStore(file.toFile(), logger)
                .save(new BiologicalClockState("world", 100, 200));
        assertEquals(100L, YamlConfiguration.loadConfiguration(file.toFile())
                .getLong("ignored-skip-offset"));
    }
}
