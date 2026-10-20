package co.surumene.wcl.api.persistence;

import java.util.Objects;
import java.util.UUID;

/** Owner-selected storage for immutable WGPF v1 genome and phenotype records.
 * This API knows nothing about worlds, entities, PDC keys or species.
 */
public interface ImmutablePayloadStore {
    /** Copy-on-write: reuse only when the previous immutable record has identical bytes. */
    UUID writeOrReuse(UUID previous, byte[] genome, byte[] phenotype);

    /** Read and verify the record. Missing or corrupt data always raises an error. */
    Payload read(UUID id);

    /** Import under an existing UUID. Returns false for an identical existing record.
     * Never overwrites a mismatching record. The caller must commit its reference only
     * after this method has returned normally.
     */
    boolean importVerified(UUID id, Payload payload);

    record Payload(byte[] genome, byte[] phenotype) {
        public Payload {
            Objects.requireNonNull(genome, "genome");
            Objects.requireNonNull(phenotype, "phenotype");
            genome = genome.clone();
            phenotype = phenotype.clone();
        }
        @Override public byte[] genome() { return genome.clone(); }
        @Override public byte[] phenotype() { return phenotype.clone(); }
    }
}
