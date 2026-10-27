package co.surumene.wcl.api.runtime;

/**
 * Pure, species-free encounter primitives. Consumer retains event sources, clock state,
 * validation/error messages, chance settings and entity spawning.
 */
public final class EncounterMath {
    private EncounterMath() {}

    /** A world-time skip cannot satisfy the natural one-tick check. */
    public static boolean naturalArrival(long previous, long current,
                                         long dayTicks, long checkTime) {
        return current == previous + 1L && Math.floorMod(current, dayTicks) == checkTime;
    }

    /** Caller validates its configured probability and [0,1) random draw. */
    public static boolean roll(double draw, double probability) {
        return draw < probability;
    }

    /** Caller validates radial and angle draws; polar mapping matches both trap types. */
    public static PlanarOffset diskOffset(double radialDraw, double angleDraw, double radius) {
        double distance = StrictMath.sqrt(radialDraw) * radius;
        double angle = angleDraw * StrictMath.PI * 2.0;
        return new PlanarOffset(distance * StrictMath.cos(angle),
                                distance * StrictMath.sin(angle));
    }

    public record PlanarOffset(double x, double z) {}
}
