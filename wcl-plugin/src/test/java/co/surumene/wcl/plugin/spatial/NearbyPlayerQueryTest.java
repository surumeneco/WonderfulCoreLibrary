package co.surumene.wcl.plugin.spatial;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NearbyPlayerQueryTest {
    @Test void queriesOnlyRequestedWorldAndRadius() {
        World world = Mockito.mock(World.class);
        Location center = Mockito.mock(Location.class);
        Player player = Mockito.mock(Player.class);
        Mockito.when(world.getNearbyPlayers(center, 12.0)).thenReturn(List.of(player));
        assertEquals(List.of(player), NearbyPlayerQuery.find(world, center, 12.0));
        Mockito.verify(world).getNearbyPlayers(center, 12.0);
    }

    @Test void exactSphereIncludesBoundaryButNotBeyond() {
        Location center = Mockito.mock(Location.class);
        Location candidate = Mockito.mock(Location.class);
        Mockito.when(center.distanceSquared(candidate)).thenReturn(25.0, 25.00001);
        assertTrue(NearbyPlayerQuery.insideSphere(center, candidate, 25.0));
        assertFalse(NearbyPlayerQuery.insideSphere(center, candidate, 25.0));
    }
}
