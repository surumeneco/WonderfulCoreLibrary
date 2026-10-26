package co.surumene.wcl.plugin.config;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/** Stateless completion of consumer-owned YAML paths. Never mutates configuration. */
public final class ConfigPathSuggestions {
    private ConfigPathSuggestions() {}

    public static List<String> keys(ConfigurationSection source, boolean includeSections,
                                    Predicate<Object> allowedValue) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(allowedValue, "allowedValue");
        TreeSet<String> found = new TreeSet<>();
        for (String key : source.getKeys(true)) {
            if (source.isConfigurationSection(key)) continue;
            Object value = source.get(key);
            if (value == null || !allowedValue.test(value)) continue;
            found.add(key);
            if (includeSections) {
                for (int position = key.indexOf('.'); position > 0;
                        position = key.indexOf('.', position + 1)) {
                    found.add(key.substring(0, position));
                }
            }
        }
        return List.copyOf(found);
    }

    public static CompletableFuture<Suggestions> suggest(
            SuggestionsBuilder builder, ConfigurationSection source,
            boolean includeSections, Predicate<Object> allowedValue) {
        Objects.requireNonNull(builder, "builder");
        String prefix = builder.getRemainingLowerCase();
        for (String key : keys(source, includeSections, allowedValue)) {
            if (key.toLowerCase(Locale.ROOT).startsWith(prefix)) builder.suggest(key);
        }
        return builder.buildFuture();
    }
}
