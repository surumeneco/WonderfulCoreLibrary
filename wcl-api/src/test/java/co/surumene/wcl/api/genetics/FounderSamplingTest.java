package co.surumene.wcl.api.genetics;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FounderSamplingTest {
    @Test void drawsTraitChoicesInOriginalOrderAndSortsByTargetId() {
        var ids = List.of("higher", "lower");
        int[] picks = {0, 1};
        AtomicInteger index = new AtomicInteger();
        var result = FounderSampling.drawTraits(ids, name -> name.equals("higher") ? 9 : 1,
                bound -> picks[index.getAndIncrement()]);
        assertEquals(List.of(new FounderSampling.TraitChoice<>("lower", false),
                new FounderSampling.TraitChoice<>("higher", false)), result);
        assertEquals(2, index.get());
    }
    @Test void duplicateIsStrongAndTwoNonePicksAreEmpty() {
        var candidates = List.of("A", "B");
        assertEquals(List.of(new FounderSampling.TraitChoice<>("A", true)),
                FounderSampling.drawTraits(candidates, String::length, bound -> 0));
        assertEquals(List.of(), FounderSampling.drawTraits(candidates,
                String::length, bound -> bound - 1));
    }
    @Test void rejectionSamplingConsumesDoublesExactlyLikeConsumers() {
        double[] draws = {0.0, 0.9, Math.exp(-0.5), 0.0};
        AtomicInteger index = new AtomicInteger();
        double value = FounderSampling.truncatedNormal(0.5, 0.2, 0.0, 1.0,
                () -> draws[index.getAndIncrement()]);
        assertEquals(0.7, value, 1e-12);
        assertEquals(4, index.get());
    }
}
