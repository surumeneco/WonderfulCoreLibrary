package co.surumene.wcl.api.runtime;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * FIFO admission queue for consumer-owned bounded worker dispatch.
 *
 * <p>This queue is not thread-safe and does not schedule, execute or poll work
 * on its own. Its owner calls {@code drain} on an enqueue/completion wakeup,
 * using its own executor capacity and generation-validity policy.
 */
public final class BoundedDispatchQueue<T> {
    private final ArrayDeque<T> waiting = new ArrayDeque<>();

    public void offer(T job) {
        waiting.addLast(Objects.requireNonNull(job, "job"));
    }

    /** Removes the first matching waiting job, if present. */
    public boolean remove(T job) {
        return waiting.remove(job);
    }

    public boolean isEmpty() {
        return waiting.isEmpty();
    }

    public int size() {
        return waiting.size();
    }

    public void clear() {
        waiting.clear();
    }

    /** Equivalent to draining with all jobs valid, for consumers without cancellation filtering. */
    public int drain(IntSupplier remainingCapacity, Consumer<? super T> submit) {
        return drain(job -> true, remainingCapacity, submit);
    }

    /**
     * Submits FIFO while capacity is available, discarding invalid jobs.
     * If submission rejects a job, restores that job to the head and stops;
     * its owner must arrange a later completion/capacity wakeup.
     *
     * <p>Only RejectedExecutionException is treated as capacity exhaustion.
     * All other callback failures propagate, matching the original consumer
     * queues. Capacity is not queried while the queue is empty.
     */
    public int drain(
            Predicate<? super T> valid,
            IntSupplier remainingCapacity,
            Consumer<? super T> submit) {
        Objects.requireNonNull(valid, "valid");
        Objects.requireNonNull(remainingCapacity, "remainingCapacity");
        Objects.requireNonNull(submit, "submit");
        int submitted = 0;
        while (!waiting.isEmpty() && remainingCapacity.getAsInt() > 0) {
            T job = waiting.removeFirst();
            if (!valid.test(job)) {
                continue;
            }
            try {
                submit.accept(job);
                submitted++;
            } catch (RejectedExecutionException rejected) {
                waiting.addFirst(job);
                break;
            }
        }
        return submitted;
    }
}
