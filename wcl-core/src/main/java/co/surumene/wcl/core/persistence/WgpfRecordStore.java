package co.surumene.wcl.core.persistence;

import co.surumene.wcl.api.persistence.ImmutablePayloadStore;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.CRC32;

/** Byte-compatible with the existing WWC/WWW WGPF v1 immutable record format.
 * Callers select the storage path and manage all entity/world reference semantics.
 */
public final class WgpfRecordStore implements ImmutablePayloadStore {
    private static final int MAGIC = 0x57475046; // WGPF
    private static final int VERSION = 1;
    private static final int HEADER_AND_CRC = 20;
    private static final int MAX_PAYLOAD = 16 * 1024 * 1024;
    private final Path directory;

    public WgpfRecordStore(Path directory) {
        this.directory = Objects.requireNonNull(directory, "directory");
    }

    @Override public synchronized UUID writeOrReuse(UUID previous, byte[] genome, byte[] phenotype) {
        Objects.requireNonNull(genome, "genome");
        Objects.requireNonNull(phenotype, "phenotype");
        validateLengths(genome.length, phenotype.length);
        if (previous != null) {
            Payload old = read(previous);
            if (Arrays.equals(old.genome(), genome) && Arrays.equals(old.phenotype(), phenotype)) {
                return previous;
            }
        }
        UUID id = UUID.randomUUID();
        importVerified(id, new Payload(genome, phenotype));
        return id;
    }

    @Override public synchronized Payload read(UUID id) {
        Objects.requireNonNull(id, "id");
        try {
            Path path = file(id);
            long length = Files.size(path);
            if (length < HEADER_AND_CRC || length > MAX_PAYLOAD + (long) HEADER_AND_CRC) {
                throw new IOException("Invalid payload file size: " + length);
            }
            return decode(Files.readAllBytes(path));
        } catch (IOException ex) {
            throw new IllegalStateException("Missing/corrupt genome payload " + id, ex);
        }
    }

    @Override public synchronized boolean importVerified(UUID id, Payload payload) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(payload, "payload");
        byte[] genome = payload.genome();
        byte[] phenotype = payload.phenotype();
        validateLengths(genome.length, phenotype.length);
        Path destination = file(id);
        if (Files.exists(destination)) {
            assertSame(id, payload, read(id));
            return false;
        }
        byte[] encoded = encode(genome, phenotype);
        Path temporary = null;
        try {
            Files.createDirectories(directory);
            temporary = Files.createTempFile(directory, id + ".", ".tmp");
            try (FileChannel channel = FileChannel.open(temporary,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                ByteBuffer buffer = ByteBuffer.wrap(encoded);
                while (buffer.hasRemaining()) channel.write(buffer);
                channel.force(true);
            }
            // An atomic move may REPLACE an existing target on some filesystems,
            // even when REPLACE_EXISTING is absent. The hard-link operation is
            // an atomic create-if-absent on the same filesystem (temp shares dir).
            // This never replaces an already committed immutable UUID.
            try {
                Files.createLink(destination, temporary);
            } catch (FileAlreadyExistsException collision) {
                assertSame(id, payload, read(id));
                return false;
            }
            assertSame(id, payload, read(id));
            return true;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not publish immutable genome payload " + id, ex);
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); } catch (IOException ignored) { }
            }
        }
    }

    private static void assertSame(UUID id, Payload expected, Payload actual) {
        if (!Arrays.equals(expected.genome(), actual.genome())
                || !Arrays.equals(expected.phenotype(), actual.phenotype())) {
            throw new IllegalStateException("Genome reference collision with different contents: " + id);
        }
    }

    private Path file(UUID id) { return directory.resolve(id.toString() + ".bin"); }

    private static void validateLengths(int genomeSize, int phenotypeSize) {
        if (genomeSize <= 0 || phenotypeSize <= 0
                || (long) genomeSize + phenotypeSize > MAX_PAYLOAD) {
            throw new IllegalArgumentException("Invalid genome/snapshot length");
        }
    }

    private static byte[] encode(byte[] genome, byte[] phenotype) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(
                    HEADER_AND_CRC + genome.length + phenotype.length);
            DataOutputStream stream = new DataOutputStream(buffer);
            stream.writeInt(MAGIC);
            stream.writeInt(VERSION);
            stream.writeInt(genome.length);
            stream.writeInt(phenotype.length);
            stream.write(genome);
            stream.write(phenotype);
            CRC32 checksum = new CRC32();
            checksum.update(buffer.toByteArray());
            stream.writeInt((int) checksum.getValue());
            return buffer.toByteArray();
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    private static Payload decode(byte[] bytes) throws IOException {
        if (bytes.length < HEADER_AND_CRC) throw new IOException("Truncated WGPF record");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        if (buffer.getInt() != MAGIC || buffer.getInt() != VERSION) {
            throw new IOException("Invalid WGPF magic or version");
        }
        int genomeSize = buffer.getInt();
        int phenotypeSize = buffer.getInt();
        try { validateLengths(genomeSize, phenotypeSize); }
        catch (IllegalArgumentException error) { throw new IOException("Invalid WGPF lengths", error); }
        if (bytes.length != (long) HEADER_AND_CRC + genomeSize + phenotypeSize) {
            throw new IOException("WGPF payload length mismatch");
        }
        CRC32 crc = new CRC32();
        crc.update(bytes, 0, bytes.length - Integer.BYTES);
        if ((int) crc.getValue() != ByteBuffer.wrap(bytes, bytes.length - 4, 4).getInt()) {
            throw new IOException("WGPF CRC32 mismatch");
        }
        byte[] genome = new byte[genomeSize];
        byte[] phenotype = new byte[phenotypeSize];
        buffer.get(genome);
        buffer.get(phenotype);
        return new Payload(genome, phenotype);
    }
}
