package co.surumene.wcl.plugin.spatial;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Objects;

/** Spatial primitives only; species-specific candidate scheduling stays with the owner. */
public final class NearbyPlayerQuery {
    private NearbyPlayerQuery() {}

    public static Collection<Player> find(World world, Location source, double radius) {
        return Objects.requireNonNull(world, "world").getNearbyPlayers(source, radius);
    }

    public static boolean insideSphere(Location source, Location player, double radiusSquared) {
        return source.distanceSquared(player) <= radiusSquared;
    }
}
