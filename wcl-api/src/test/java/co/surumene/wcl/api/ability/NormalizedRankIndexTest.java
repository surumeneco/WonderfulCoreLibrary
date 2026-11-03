package co.surumene.wcl.api.ability;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
final class NormalizedRankIndexTest {
    @Test void preservesElevenRanksAndThresholdEdges() {
        assertEquals(0,NormalizedRankIndex.of(0, false));
        assertEquals(8,NormalizedRankIndex.of(1, false));
        assertEquals(9,NormalizedRankIndex.of(1.25, false));
        assertEquals(10,NormalizedRankIndex.of(Math.nextUp(1.25), false));
        assertEquals(0,NormalizedRankIndex.of(Double.NaN, false));
        assertEquals(10,NormalizedRankIndex.of(Double.POSITIVE_INFINITY, false));
        assertEquals(0,NormalizedRankIndex.of(Double.NEGATIVE_INFINITY, false));
    }
    @Test void strictWolfRejectsNonfiniteValuesWithoutChangingValidRanks() {
        for(double d : new double[]{0,0.1,0.5,1,1.1,1.25,1.5})
            assertEquals(NormalizedRankIndex.of(d,false),NormalizedRankIndex.of(d,true));
        for(double d : new double[]{Double.NaN,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY})
            assertThrows(IllegalArgumentException.class,()->NormalizedRankIndex.of(d,true));
    }
}
