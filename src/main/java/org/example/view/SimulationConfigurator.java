package org.example.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Math.pow;

public class SimulationConfigurator {
    private JFrame configFrame;
    private JTextField physicalPageSizeField;
    private JTextField offsetBitsField;
    private JTextField virtualMemorySizeField;
    private JTextField tlbEntriesField;

    public SimulationConfigurator() {
        createConfigGUI();
    }

    private void createConfigGUI() {
        configFrame = new JFrame("Memory Simulator - Configuration");
        configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configFrame.setSize(500, 450);
        configFrame.setLayout(new BorderLayout());
        configFrame.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(130, 130, 130));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Memory Simulator Configuration");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setBorder(new EmptyBorder(30, 50, 20, 50));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(createFormRow("Physical Memory Size (bytes):", "16"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        formPanel.add(createFormRow("Offset Bits:", "2"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        formPanel.add(createFormRow("Virtual Memory Size (bytes):", "32"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        formPanel.add(createFormRow("TLB Entries:", "4"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 245, 255));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 200)),
                "Calculation Info",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("SansSerif", Font.PLAIN, 12)
        ));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel infoLabel1 = new JLabel("• Page Size = 2^(Offset Bits) bytes");
        JLabel infoLabel2 = new JLabel("• Virtual Pages = Virtual Memory Size / Page Size");
        JLabel infoLabel3 = new JLabel("• Physical Memory Pages =Physical Memory Size / Page Size");

        infoLabel1.setFont(new Font("SansSerif", Font.PLAIN, 11));
        infoLabel2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        infoLabel3.setFont(new Font("SansSerif", Font.PLAIN, 11));



        infoPanel.add(infoLabel1);
        infoPanel.add(infoLabel2);
        infoPanel.add(infoLabel3);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        formPanel.add(infoPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton startButton = createActionButton("Start Simulation", new Color(100, 100, 120));
        JButton calculateButton = createActionButton("Calculate", new Color(100, 100, 120));

        buttonPanel.add(calculateButton);
        buttonPanel.add(startButton);

        configFrame.add(headerPanel, BorderLayout.NORTH);
        configFrame.add(formPanel, BorderLayout.CENTER);
        configFrame.add(buttonPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startSimulation());
                    calculateButton.addActionListener(e -> showCalculations());

        configFrame.getRootPane().setDefaultButton(startButton);

        configFrame.setVisible(true);
    }

    private JPanel createFormRow(String labelText, String defaultValue) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(new Color(245, 245, 250));
        rowPanel.setMaximumSize(new Dimension(400, 40));

        JLabel label = createFormLabel(labelText);
        rowPanel.add(label, BorderLayout.WEST);

        JTextField textField = createFormTextField(defaultValue);

        switch (labelText) {
            case "Physical Memory Size (bytes):":
                physicalPageSizeField = textField;
                break;
            case "Offset Bits:":
                offsetBitsField = textField;
                break;
            case "Virtual Memory Size (bytes):":
                virtualMemorySizeField = textField;
                break;
            case "TLB Entries:":
                tlbEntriesField = textField;
                break;
        }

        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        fieldPanel.setBackground(new Color(245, 245, 250));
        fieldPanel.add(textField);

        rowPanel.add(fieldPanel, BorderLayout.CENTER);

        return rowPanel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 80));
        label.setPreferredSize(new Dimension(200, 30));
        return label;
    }

    private JTextField createFormTextField(String defaultValue) {
        JTextField field = new JTextField(defaultValue, 10);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(120, 35));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        field.setFocusable(true);
        field.setEditable(true);
        field.setEnabled(true);

        return field;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    public void setDefaultValues() {
        if (physicalPageSizeField != null) physicalPageSizeField.setText("32");
        if (offsetBitsField != null) offsetBitsField.setText("1");
        if (virtualMemorySizeField != null) virtualMemorySizeField.setText("64");
        if (tlbEntriesField != null) tlbEntriesField.setText("4");
    }

    private void showCalculations() {
        try {
            MemoryConfiguration config = parseInputs();
            if (config != null) {
                showCalculationResults(config);
            }
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers for all fields.");
        }
    }

    private MemoryConfiguration parseInputs() {
        while (true) {
            try {
                int physicalPageSize = Integer.parseInt(physicalPageSizeField.getText().trim());
                int offsetBits = Integer.parseInt(offsetBitsField.getText().trim());
                int virtualMemorySize = Integer.parseInt(virtualMemorySizeField.getText().trim());
                int tlbEntries = Integer.parseInt(tlbEntriesField.getText().trim());

                if (physicalPageSize <= 0 || offsetBits <= 0 || virtualMemorySize <= 0 || tlbEntries <= 0) {
                    showError("All values must be positive integers.");
                    return null;
                }

                if (!isPowerOfTwo(physicalPageSize)) {
                    showError("Physical Memory size must be a power of 2.");
                    return null;
                }

                if (!isPowerOfTwo(virtualMemorySize)) {
                    showError("Virtual memory size must be a power of 2.");
                    return null;
                }


                int ptSize = (int) (virtualMemorySize / pow(2, offsetBits));
                int physicalSize = (int) (physicalPageSize/pow(2, offsetBits));

                return new MemoryConfiguration(physicalPageSize, offsetBits, physicalSize, ptSize, tlbEntries);

            } catch (NumberFormatException ex) {
                showError("Please enter valid integers for all fields.");
                return null;
            }
        }
    }

    private boolean isPowerOfTwo(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }

    private void showCalculationResults(MemoryConfiguration config) {


        String results = String.format(
                "Calculated Memory Configuration:\n\n" +
                        "• Page Size: %,d bytes\n" +
                        "• Offset bits: %d bits\n" +
                        "• Page Table Entries: %d entries\n" +
                        "• Physical Memory: %d bytes\n" +
                        "• TLB Entries: %d entries\n\n",
                config.physicalPageSize,
                config.offsetBits,
                config.ptSize,
                config.physicalSize,
                config.tlbEntries
        );

        JOptionPane.showMessageDialog(configFrame, results, "Calculation Results",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void startSimulation() {
        MemoryConfiguration config = parseInputs();
        if (config != null) {
            configFrame.dispose();


            startMemorySimulation(config);
        }
    }

    private void startMemorySimulation(MemoryConfiguration config) {
        SwingUtilities.invokeLater(() -> {
            new MemorySimulatorGUI(config);
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(configFrame, message, "Configuration Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static class MemoryConfiguration {
        public final int physicalPageSize;
        public int offsetBits;
        public int physicalSize;
        public int ptSize;
        public int tlbEntries;

        public MemoryConfiguration(int physicalPageSize, int offsetBits, int physicalSize, int ptSize, int tlbEntries) {
            this.physicalPageSize = physicalPageSize;
            this.offsetBits = offsetBits;
            this.physicalSize = physicalSize;
            this.ptSize = ptSize;
            this.tlbEntries = tlbEntries;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimulationConfigurator();
        });
    }
}