package co.surumene.wcl.api.time;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class AgeCurveMathTest {
    @Test void matchesChickenAndWolfMidrangeEndpointsAndSmoothMidpoints() {
        assertEquals(0.5, AgeCurveMath.rankReductionAt(0, 672, 0.5, 3552, 7872, 1.5));
        assertEquals(0.25, AgeCurveMath.rankReductionAt(336, 672, 0.5, 3552, 7872, 1.5));
        assertEquals(0, AgeCurveMath.rankReductionAt(672, 672, 0.5, 3552, 7872, 1.5));
        assertEquals(0, AgeCurveMath.rankReductionAt(3552, 672, 0.5, 3552, 7872, 1.5));
        assertEquals(0.75, AgeCurveMath.rankReductionAt(5712, 672, 0.5, 3552, 7872, 1.5));
        assertEquals(1.5, AgeCurveMath.rankReductionAt(7872, 672, 0.5, 3552, 7872, 1.5));
    }

    @Test void excludesNonfiniteInputAndHandlesInstantGrowth() {
        assertThrows(IllegalArgumentException.class, () ->
                AgeCurveMath.rankReductionAt(Double.NaN, 672, 0.5, 3552, 7872, 1.5));
        assertEquals(0, AgeCurveMath.rankReductionAt(0, 0, 0.5, 10, 20, 1.5));
        assertEquals(1.5, AgeCurveMath.rankReductionAt(20, 0, 0.5, 10, 20, 1.5));
    }
}
