import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AgingScheduler {

    private final ERSystem er;
    private final ScheduledExecutorService scheduler;
    private final int intervalSeconds;

    public AgingScheduler(ERSystem er, int intervalSeconds) {
        this.er = er;
        this.intervalSeconds = intervalSeconds;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (er) {
                if (!er.getQueue().isEmpty()) {
                    er.getQueue().reheapifyAll();
                    System.out.println("\n  [Aging tick] Scores updated.");
                }
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }
    public void stop() {
        scheduler.shutdown();
    }
}