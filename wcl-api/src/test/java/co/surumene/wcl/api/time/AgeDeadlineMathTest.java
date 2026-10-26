package co.surumene.wcl.api.time;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
final class AgeDeadlineMathTest {
    @Test void nextRefreshAndInjuryOnsetRemainSemanticallyEquivalent(){
        for(var mode:AgeDeadlineMath.BoundaryMode.values()){
            assertEquals(400,AgeDeadlineMath.next(205,0,1.0,5.0,10.0,List.of(),mode));
            assertEquals(5*24000,AgeDeadlineMath.next(24000,0,1.0,5.0,10.0,List.of(),mode));
            assertEquals(400,AgeDeadlineMath.next(205,0,1.0,5.0,10.0,List.of(0.5),mode));
            assertEquals(Long.MAX_VALUE,AgeDeadlineMath.next(24000*12L,0,1.0,5.0,10.0,List.of(),mode));
        }
    }
    @Test void negativeStartBoundaryGuardPreservesLegacyVersusGuardedPolicy(){
        assertEquals(Long.MAX_VALUE,AgeDeadlineMath.next(24000,Long.MIN_VALUE,
                0.0,1.0,2.0,List.of(),AgeDeadlineMath.BoundaryMode.LEGACY));
        assertEquals(Long.MAX_VALUE,AgeDeadlineMath.next(24000,Long.MIN_VALUE,
                0.0,1.0,2.0,List.of(),AgeDeadlineMath.BoundaryMode.GUARDED));
    }
}
