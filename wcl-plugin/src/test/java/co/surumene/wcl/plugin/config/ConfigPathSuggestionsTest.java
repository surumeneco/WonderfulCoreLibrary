package co.surumene.wcl.plugin.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Set;

final class ConfigPathSuggestionsTest {
    @Test void listsLeavesAndSectionsFromActualConfiguration() {
        var yaml = new YamlConfiguration();
        yaml.set("stats.speed.min", 0.1);
        yaml.set("stats.speed.max", 1.5);
        yaml.set("stats.speed.display", "rank");
        yaml.set("runtime.enabled", true);
        assertEquals(Set.of("stats.speed.min", "stats.speed.max", "stats.speed.display",
                "runtime.enabled"), Set.copyOf(ConfigPathSuggestions.keys(yaml, false, value -> true)));
        assertTrue(ConfigPathSuggestions.keys(yaml, true, value -> true).contains("stats.speed"));
        assertEquals(Set.of("stats.speed.min", "stats.speed.max"),
                Set.copyOf(ConfigPathSuggestions.keys(yaml, false, value -> value instanceof Number)));
        assertFalse(ConfigPathSuggestions.keys(yaml, false, value -> true).contains("stats.speed"));
    }

    @Test void offersOnlyKeysMatchingTypedPrefixAndNeverWrites() {
        var yaml = new YamlConfiguration();
        yaml.set("stats.speed.min", 0.1);
        yaml.set("stats.speed.max", 1.5);
        yaml.set("runtime.enabled", true);
        var builder = new com.mojang.brigadier.suggestion.SuggestionsBuilder("stats.s", 0);
        var future = ConfigPathSuggestions.suggest(builder, yaml, false, value -> true);
        assertEquals(Set.of("stats.speed.min", "stats.speed.max"),
                future.join().getList().stream().map(s -> s.getText()).collect(java.util.stream.Collectors.toSet()));
        assertEquals(0.1, yaml.getDouble("stats.speed.min"));
    }

    @Test void sectionsForNumericResetHaveNestedMatchingValues() {
        var yaml = new YamlConfiguration();
        yaml.set("stats.speed.min", 0.1);
        yaml.set("stats.speed.display", "rank");
        yaml.set("message.text", "hello");
        var sections = ConfigPathSuggestions.keys(yaml, true, value -> value instanceof Number);
        assertTrue(sections.contains("stats"));
        assertTrue(sections.contains("stats.speed"));
        assertFalse(sections.contains("message"));
        assertFalse(sections.contains("stats.speed.display"));
    }
}
