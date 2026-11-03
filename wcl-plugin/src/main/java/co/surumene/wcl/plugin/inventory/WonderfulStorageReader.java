package co.surumene.wcl.plugin.inventory;
import co.surumene.wcl.api.inventory.StorageReadResult;
import co.surumene.wcl.api.inventory.StorageSnapshot;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.persistence.PersistentDataContainer;
/** Each WWC/WWW consumer registers one Bukkit ServicesManager provider.
 * All calls require the Paper primary thread. readStored accepts PDC supplied by an
 * independent offline-data reader and NEVER loads chunks, modifies PDC, or writes files.
 */
public interface WonderfulStorageReader {
    String providerId();
    StorageReadResult readLoaded(UUID entityId);
    StorageReadResult readStored(UUID entityId, UUID worldId,
            Optional<StorageSnapshot.Position> position, PersistentDataContainer pdc);
}
