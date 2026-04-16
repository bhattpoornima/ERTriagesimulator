import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AgingScheduler {

    private final TriageHeap heap;
    private final ScheduledExecutorService scheduler;
    private final int intervalSeconds;

    public AgingScheduler(TriageHeap heap, int intervalSeconds) {
        this.heap = heap;
        this.intervalSeconds = intervalSeconds;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (heap) {
                if (!heap.isEmpty()) {
                    heap.reheapifyAll(); // recalculate all scores (wait time increased)
                    System.out.println("\n  [Aging tick] Scores updated for " + heap.size() + " waiting patients.");
                }
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }
}