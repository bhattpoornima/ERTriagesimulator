public class Patient implements Comparable<Patient> {

    public enum Severity {
        CRITICAL(5), SEVERE(4), MODERATE(3), MILD(2), MINOR(1);

        public final int value;
        Severity(int value) { this.value = value; }
    }

    private static int idCounter = 1;

    private final int id;
    private final String name;
    private final int age;
    private final Severity severity;
    private final long arrivalTime;      // System.currentTimeMillis()
    private double priorityScore;

    public Patient(String name, int age, Severity severity) {
        this.id = idCounter++;
        this.name = name;
        this.age = age;
        this.severity = severity;
        this.arrivalTime = System.currentTimeMillis();
        this.priorityScore = calculateScore();
    }

    // Multi-key scoring formula — the heart of the system
    public double calculateScore() {
        double waitMinutes = (System.currentTimeMillis() - arrivalTime) / 60000.0;
        double ageFactor   = (age >= 60 || age <= 12) ? 1.5 : 1.0; // elderly/children boosted

        this.priorityScore = (severity.value * 10.0)   // weight: 50%
                           + (ageFactor * 5.0)          // weight: 25%
                           + (waitMinutes * 2.0);       // weight: 25% — grows over time!
        return this.priorityScore;
    }

    @Override
    public int compareTo(Patient other) {
        return Double.compare(other.priorityScore, this.priorityScore); // max-heap order
    }

    // Getters
    public int getId()               { return id; }
    public String getName()          { return name; }
    public int getAge()              { return age; }
    public Severity getSeverity()    { return severity; }
    public long getArrivalTime()     { return arrivalTime; }
    public double getPriorityScore() { return priorityScore; }

    @Override
    public String toString() {
        return String.format("[#%d] %-15s | %s | Age: %2d | Score: %.1f",
                id, name, severity, age, priorityScore);
    }
}