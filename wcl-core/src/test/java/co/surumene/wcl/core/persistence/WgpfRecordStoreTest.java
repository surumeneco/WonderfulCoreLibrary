package co.surumene.wcl.core.persistence;

import co.surumene.wcl.api.persistence.ImmutablePayloadStore;
import co.surumene.wcl.core.runtime.DefaultConsumerContext;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class WgpfRecordStoreTest {
    @TempDir Path temp;
    private static final byte[] GENOME = {1, 2, 3, 4};
    private static final byte[] PHENOTYPE = {5, 6, 7};

    @Test void copiesLegacyV1RecordWithSameUuidWithoutRemovingSource() throws Exception {
        var old = new WgpfRecordStore(temp.resolve("old/genomes"));
        var next = new WgpfRecordStore(temp.resolve("world-two/genomes"));
        UUID id = old.writeOrReuse(null, GENOME, PHENOTYPE);
        byte[] sourceBytes = Files.readAllBytes(temp.resolve("old/genomes").resolve(id + ".bin"));
        assertEquals(0x57475046, ByteBuffer.wrap(sourceBytes).getInt());
        assertEquals(1, ByteBuffer.wrap(sourceBytes).getInt(4));
        assertTrue(next.importVerified(id, old.read(id)));
        assertArrayEquals(sourceBytes, Files.readAllBytes(temp.resolve("world-two/genomes").resolve(id + ".bin")));
        assertTrue(Files.exists(temp.resolve("old/genomes").resolve(id + ".bin")));
        assertFalse(next.importVerified(id, old.read(id)));
    }

    @Test void neverOverwritesAnExistingDifferentRecord() throws Exception {
        var target = new WgpfRecordStore(temp.resolve("dest"));
        UUID id = UUID.randomUUID();
        assertTrue(target.importVerified(id, new ImmutablePayloadStore.Payload(GENOME, PHENOTYPE)));
        byte[] before = Files.readAllBytes(temp.resolve("dest").resolve(id + ".bin"));
        assertThrows(IllegalStateException.class,
                () -> target.importVerified(id, new ImmutablePayloadStore.Payload(new byte[] {99}, PHENOTYPE)));
        assertArrayEquals(before, Files.readAllBytes(temp.resolve("dest").resolve(id + ".bin")));
    }

    @Test void rejectsCorruptPayloadAndNeverOverwritesIt() throws Exception {
        var source = new WgpfRecordStore(temp);
        UUID id = source.writeOrReuse(null, GENOME, PHENOTYPE);
        Path path = temp.resolve(id + ".bin");
        byte[] corrupt = Files.readAllBytes(path);
        corrupt[18] ^= 1;
        Files.write(path, corrupt);
        assertThrows(IllegalStateException.class, () -> source.read(id));
        assertThrows(IllegalStateException.class, () -> source.importVerified(id,
                new ImmutablePayloadStore.Payload(GENOME, PHENOTYPE)));
        assertArrayEquals(corrupt, Files.readAllBytes(path));
    }

    @Test void payloadArraysAreDefensivelyCopiedAndReuseIsStable() {
        var store = new WgpfRecordStore(temp);
        UUID id = store.writeOrReuse(null, GENOME, PHENOTYPE);
        var payload = store.read(id);
        payload.genome()[0] = 9;
        payload.phenotype()[0] = 9;
        assertArrayEquals(GENOME, store.read(id).genome());
        assertEquals(id, store.writeOrReuse(id, GENOME, PHENOTYPE));
        assertThrows(IllegalArgumentException.class,
                () -> store.writeOrReuse(null, new byte[0], PHENOTYPE));
    }

    @Test void ownerContextOpensIndependentStores() throws Exception {
        var first = new DefaultConsumerContext();
        var second = new DefaultConsumerContext();
        var a = first.newPayloadStore(temp.resolve("a"));
        var b = second.newPayloadStore(temp.resolve("b"));
        UUID id = a.writeOrReuse(null, GENOME, PHENOTYPE);
        assertThrows(IllegalStateException.class, () -> b.read(id));
        first.close();
        assertTrue(b.importVerified(id, a.read(id)));
    }
}
