import java.util.*;

public class ERSystem {

    private final TriageHeap triageQueue;
    private final int totalDoctors;
    private int availableDoctors;
    private int totalTreated;
    private long totalWaitTimeMs;

    public ERSystem(int numDoctors) {
        this.triageQueue     = new TriageHeap();
        this.totalDoctors    = numDoctors;
        this.availableDoctors = numDoctors;
        this.totalTreated    = 0;
        this.totalWaitTimeMs = 0;
    }

    public synchronized void admit(Patient p) {
        triageQueue.insert(p);
        System.out.println("  Admitted: " + p);
        tryTreat();
    }

    public synchronized void tryTreat() {
        while (availableDoctors > 0 && !triageQueue.isEmpty()) {
            triageQueue.reheapifyAll();
            Patient next = triageQueue.poll();
            availableDoctors--;
            long waitMs = System.currentTimeMillis() - next.getArrivalTime();
            totalWaitTimeMs += waitMs;
            totalTreated++;

            System.out.printf("  TREATING: %s | Wait: %.1f min%n",
                    next, waitMs / 60000.0);

            // Simulate treatment duration (in real sim, use a timer)
            new Thread(() -> {
                try {
                    int treatMs = getTreatmentDuration(next.getSeverity());
                    Thread.sleep(treatMs);
                } catch (InterruptedException ignored) {}
                synchronized (this) {
                    availableDoctors++;
                    System.out.println("  Doctor free. Checking queue...");
                    tryTreat();
                }
            }).start();
        }
    }

    private int getTreatmentDuration(Patient.Severity s) {
        return switch (s) {
            case CRITICAL -> 50000;
            case SEVERE   -> 40000;
            case MODERATE -> 30000;
            case MILD     -> 20000;
            case MINOR    -> 10000;
        };
    }

    public void printStats() {
        System.out.println("\n━━━━ ER Session Statistics ━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  Patients treated : " + totalTreated);
        System.out.println("  Still waiting    : " + triageQueue.size());
        if (totalTreated > 0)
            System.out.printf("  Avg wait time    : %.1f min%n",
                    (totalWaitTimeMs / totalTreated) / 60000.0);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    public TriageHeap getQueue() { return triageQueue; }

    public synchronized boolean isAllDone() {
        return triageQueue.isEmpty() && availableDoctors == totalDoctors;
    }

    public int getTotalTreated() { return totalTreated; }
    public long getTotalWaitTimeMs() { return totalWaitTimeMs; }
}