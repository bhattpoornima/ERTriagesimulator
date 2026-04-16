import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("━━━━ ER Triage System Started ━━━━━━━━━━━━━━━━━━");
        ERSystem er = new ERSystem(2); // 2 doctors on duty

        // Start aging scheduler — boosts scores every 30 seconds
        AgingScheduler aging = new AgingScheduler(er, 30);
        aging.start();
        //launch UI
        SwingUtilities.invokeLater(() -> new ERTriageInterface(er));

        // Keep running (aging scheduler in background)
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            aging.stop();
            er.printStats();
        }
    }
}