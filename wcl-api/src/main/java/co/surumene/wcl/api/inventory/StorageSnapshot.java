package co.surumene.wcl.api.inventory;
import java.util.*;
/** Point-in-time standalone values. Do not flatten nested container contents: callers traverse them once. */
public record StorageSnapshot(UUID entityId, String providerId, UUID worldId,
        Optional<Position> position, Map<String,UUID> relationships, List<StoredItem> items) {
    public record Position(double x,double y,double z) {
        public Position {if(!Double.isFinite(x)||!Double.isFinite(y)||!Double.isFinite(z))throw new IllegalArgumentException("nonfinite position");}
    }
    public StorageSnapshot {
        Objects.requireNonNull(entityId);
        if (Objects.requireNonNull(providerId).isBlank())throw new IllegalArgumentException("providerId");
        Objects.requireNonNull(worldId);
        position=Objects.requireNonNull(position);
        relationships=Map.copyOf(Objects.requireNonNull(relationships));
        items=List.copyOf(Objects.requireNonNull(items));
        HashSet<String> names=new HashSet<>();
        for (StoredItem item:items) if(!names.add(item.slot()))throw new IllegalArgumentException("duplicate storage slot "+item.slot());
    }
}
