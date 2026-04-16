public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("━━━━ ER Triage System Started ━━━━━━━━━━━━━━━━━━");
        ERSystem er = new ERSystem(2); // 2 doctors on duty

        // Start aging scheduler — boosts scores every 30 seconds
        AgingScheduler aging = new AgingScheduler(er, 30);
        aging.start();

        // Simulate a wave of patients arriving
        er.admit(new Patient("Raj Kumar",    45, Patient.Severity.MODERATE));
        er.admit(new Patient("Priya Sharma", 72, Patient.Severity.MILD));     // elderly — boosted
        er.admit(new Patient("Arjun Singh",  28, Patient.Severity.CRITICAL));
        er.admit(new Patient("Meera Nair",    8, Patient.Severity.SEVERE));   // child — boosted
        er.admit(new Patient("Anil Gupta",   55, Patient.Severity.MINOR));

        System.out.println("\n  Queue after initial admissions:");
        er.printQueue();

        Thread.sleep(2000);

        // More patients arrive mid-simulation
        er.admit(new Patient("Sunita Rao",   65, Patient.Severity.SEVERE));
        er.admit(new Patient("Vikram Bose",  33, Patient.Severity.CRITICAL));

        Thread.sleep(3000);

        System.out.println("\n  Queue mid-simulation:");
        er.printQueue();

        Thread.sleep(5000);

        aging.stop();

        // Wait until all treatments complete
        while (!er.isAllDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        er.printStats(); // FINAL stats
    }
}