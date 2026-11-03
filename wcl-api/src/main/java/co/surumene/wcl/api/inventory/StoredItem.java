package co.surumene.wcl.api.inventory;
import java.util.Objects;
import java.util.Arrays;
/** Serialized Paper ItemStack; metadata/PDC and nested containers remain inside the byte payload. */
public final class StoredItem {
    private final String slot;
    private final byte[] bytes;
    public StoredItem(String slot, byte[] bytes) {
        this.slot=Objects.requireNonNull(slot, "slot");
        this.bytes=Objects.requireNonNull(bytes,"bytes").clone();
        if (slot.isBlank() || bytes.length==0) throw new IllegalArgumentException("empty slot/item");
    }
    public String slot() {return slot;}
    public byte[] bytes(){return bytes.clone();}
    @Override public boolean equals(Object value){return value instanceof StoredItem other && slot.equals(other.slot) && Arrays.equals(bytes,other.bytes);}
    @Override public int hashCode(){return 31*slot.hashCode()+Arrays.hashCode(bytes);}
}
