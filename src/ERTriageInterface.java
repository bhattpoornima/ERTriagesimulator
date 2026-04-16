import javax.swing.*;
import java.awt.*;

public class ERTriageInterface {

    private ERSystem er;

    public ERTriageInterface(ERSystem er) {
        this.er = er;

        JFrame frame = new JFrame("ER Triage System");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ─── Input Panel ───
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));  // Fixed: 5 rows for 10 components

        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<Patient.Severity> severityBox =
                new JComboBox<>(Patient.Severity.values());

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("Severity:"));
        inputPanel.add(severityBox);

        JButton addButton = new JButton("Add Patient");
        JButton showQueueBtn = new JButton("Show Queue");
        JButton statsBtn = new JButton("Show Stats");

        inputPanel.add(addButton);
        inputPanel.add(showQueueBtn);
        inputPanel.add(new JLabel());  // Spacer
        inputPanel.add(statsBtn);

        frame.add(inputPanel, BorderLayout.NORTH);

        // ─── Output Area ───
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        frame.add(scrollPane, BorderLayout.CENTER);

        // ─── Button Actions ───
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Name cannot be empty");
                    return;
                }

                String ageText = ageField.getText().trim();
                if (ageText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Age cannot be empty");
                    return;
                }

                int age = Integer.parseInt(ageText);
                if (age < 0 || age > 150) {
                    JOptionPane.showMessageDialog(frame, "Age must be between 0 and 150");
                    return;
                }

                Patient.Severity severity = (Patient.Severity) severityBox.getSelectedItem();

                Patient p = new Patient(name, age, severity);
                er.admit(p);

                outputArea.append("Admitted: " + p + "\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());  // Auto-scroll

                nameField.setText("");
                ageField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Age must be a valid number");
            }
        });

        showQueueBtn.addActionListener(e -> {
            outputArea.append("\n--- CURRENT QUEUE ---\n");
            outputArea.append(getQueueDisplay() + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });

        statsBtn.addActionListener(e -> {
            outputArea.append("\n--- CURRENT STATS ---\n");
            outputArea.append(getStatsDisplay() + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });

        frame.setVisible(true);
    }

    private String getQueueDisplay() {
        if (er.getQueue().isEmpty()) {
            return "(queue is empty)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("┌─ Triage Queue (highest priority first) ─────────────────┐\n");
        er.getQueue().getSortedPatients()
            .forEach(p -> sb.append("│  ").append(p).append("\n"));
        sb.append("└──────────────────────────────────────────────────────────┘");
        return sb.toString();
    }

    private String getStatsDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patients treated : ").append(er.getTotalTreated()).append("\n");
        sb.append("In treatment     : ").append(er.getInTreatment()).append("\n");
        sb.append("Still waiting    : ").append(er.getQueue().size()).append("\n");
        if (er.getTotalTreated() > 0) {
            double avgWait = (double) er.getTotalWaitTimeMs() / er.getTotalTreated() / 60000.0;
            sb.append(String.format("Avg wait time    : %.1f min\n", avgWait));
        }
        return sb.toString();
    }
}
