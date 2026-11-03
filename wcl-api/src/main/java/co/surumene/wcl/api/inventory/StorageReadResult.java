package co.surumene.wcl.api.inventory;
import java.util.Objects;
/** Missing individual is not an empty inventory; errors are never silently converted to empty. */
public sealed interface StorageReadResult permits StorageReadResult.Success,StorageReadResult.NotFound,StorageReadResult.Failure {
    record Success(StorageSnapshot snapshot) implements StorageReadResult {
        public Success {Objects.requireNonNull(snapshot);}
    }
    record NotFound() implements StorageReadResult {}
    record Failure(String code,String detail) implements StorageReadResult {
        public Failure {if(Objects.requireNonNull(code).isBlank())throw new IllegalArgumentException("code");Objects.requireNonNull(detail);}
    }
}
