package co.surumene.wcl.api.time;
import java.util.List;
import java.util.Objects;
/** Identical nominal age/injury schedule, retaining caller-specific historical overflow behavior. */
public final class AgeDeadlineMath {
    private static final double TICKS_PER_DAY=24000.0;
    private static final long QUANTUM=200L;
    public enum BoundaryMode { LEGACY, GUARDED }
    private AgeDeadlineMath(){}
    public static long next(long now,long adultStart,double growthEnd,double agingStart,
                            double elderDay,List<Double> injuryOnsets,BoundaryMode mode){
        Objects.requireNonNull(injuryOnsets);
        Objects.requireNonNull(mode);
        double days=Math.max(0,now-adultStart)/TICKS_PER_DAY;
        long due;
        if(days<growthEnd || (days>=agingStart && days<elderDay)){
            due=(now/QUANTUM+1L)*QUANTUM;
        }else if(days<agingStart){
            due=boundary(adultStart,agingStart,mode);
        }else{
            due=Long.MAX_VALUE;
        }
        for(Double onset:injuryOnsets){
            if(onset!=null && Double.isFinite(onset) && onset>days)
                due=Math.min(due,boundary(adultStart,onset,mode));
        }
        return due;
    }
    private static long boundary(long start,double days,BoundaryMode mode){
        if(mode==BoundaryMode.LEGACY){
            if(!Double.isFinite(days) || days>(Long.MAX_VALUE-start)/TICKS_PER_DAY)
                return Long.MAX_VALUE;
            return start+(long)Math.ceil(days*TICKS_PER_DAY);
        }
        if(!Double.isFinite(days)||days<0||
                days>(Long.MAX_VALUE-Math.max(0L,start))/TICKS_PER_DAY)
            return Long.MAX_VALUE;
        return Math.max(start,start+(long)Math.ceil(days*TICKS_PER_DAY));
    }
}
