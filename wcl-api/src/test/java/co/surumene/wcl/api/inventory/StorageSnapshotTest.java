package co.surumene.wcl.api.inventory;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class StorageSnapshotTest {
    @Test void itemBytesCannotBeMutatedAndNestedContentIsKeptOpaque() {
        byte[] original={1,2,3};
        StoredItem item=new StoredItem("shulker",original);
        original[0]=9;
        item.bytes()[1]=9;
        assertArrayEquals(new byte[]{1,2,3},item.bytes());
    }
    @Test void recordMakesCopiesAndRejectsDuplicateSlots() {
        Map<String,UUID> relation=new HashMap<>();
        List<StoredItem> items=new ArrayList<>();
        items.add(new StoredItem("weapon",new byte[]{8}));
        var record=new StorageSnapshot(UUID.randomUUID(),"www",UUID.randomUUID(),
                Optional.empty(),relation,items);
        relation.put("owner",UUID.randomUUID());
        items.clear();
        assertEquals(1,record.items().size());
        assertTrue(record.relationships().isEmpty());
        assertThrows(IllegalArgumentException.class,()->new StorageSnapshot(
                UUID.randomUUID(),"www",UUID.randomUUID(),Optional.empty(),Map.of(),
                List.of(new StoredItem("weapon",new byte[]{1}),new StoredItem("weapon",new byte[]{2}))));
        assertTrue(new StorageReadResult.Failure("CORRUPT","bad") instanceof StorageReadResult);
    }
}
