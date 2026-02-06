package org.example.view;


import org.example.demo.AutomatedDemoSimulator;
import org.example.demo.DemoScenarios;
import org.example.manager.MemoryManager;
import org.example.manager.Replacement;
import org.example.model.*;
import org.example.utilis.MessageDispatcher;
import org.example.utilis.StepByStepMessageDispatcher;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MemorySimulatorGUI {

    public int noHit=0;
    public int no_queries=0;
    JLabel hitMissLabel;
    private JLabel addressDecompositionLabel;
    public  Integer frameFound;
    public Integer pageIntroduced;
    private PhysicalMemory pm;
    private PageTable pt;
    private TLB tlb;
    private Disk disk;
    private Replacement r;
    private MemoryManager mm;
    private int offsetBits;
    private JTextPane tlbArea;
    private JTextPane ramArea;
    private JTextPane diskArea;
    private JTextPane ptArea;
    private JLabel statusLabel;
    public JButton nextStepButton;
    public JButton fetchButton;
    public JTextField pageInput;
    JFrame mainFrame;
    private JWindow decompositionWindow;
    private JTextPane addressDecompositionPane;
    public MemoryStateSnapshot lastSnap;

    public MemorySimulatorGUI(SimulationConfigurator.MemoryConfiguration config) {
        pm = new PhysicalMemory(config.physicalSize);
        pt = new PageTable(config.ptSize);
        tlb = new TLB(config.tlbEntries);
        disk = new Disk(config.ptSize);
        r = new Replacement();
        mm = new MemoryManager(this);
        offsetBits = config.offsetBits;
        MessageDispatcher.initialize(this::showStatusMessage);
        StepByStepMessageDispatcher.initialize(this::showStatusMessage);

        createGUI();

    }

    private void createGUI() {
        JFrame frame = new JFrame("Virtual Memory Simulator");
        this.mainFrame = frame;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 250));

        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton helpButton = new JButton("?");
        helpButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        helpButton.setBackground(new Color(180, 180, 180));
        helpButton.setForeground(Color.BLACK);
        helpButton.setFocusPainted(false);
        helpButton.setPreferredSize(new Dimension(60, 30));
       helpPanel.add(helpButton);
        frame.add(helpPanel, BorderLayout.NORTH);



        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        statusPanel.setBackground(new Color(245, 245, 250));
        statusLabel = new JLabel("Virtual Memory Simulator");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(new Color(80, 80, 80));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);


        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        inputPanel.setBackground(new Color(245, 245, 250));
        JLabel inputLabel = new JLabel("Enter Page:");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        pageInput = new JTextField(10);
        pageInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pageInput.setPreferredSize(new Dimension(80, 30));

        JButton demoButton = new JButton("Auto Demo");

        demoButton.setBackground(new Color(60, 120, 200));
        demoButton.setForeground(Color.WHITE);
        demoButton.setFocusPainted(false);
        demoButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        demoButton.setPreferredSize(new Dimension(120, 30));
        demoButton.addActionListener(e -> showScenarioSelectionDialog());
        inputLabel.add(Box.createRigidArea(new Dimension(10, 4)));
        inputLabel.add(demoButton);

        fetchButton = new JButton("Fetch Page");
        fetchButton.setBackground(new Color(90, 100, 100));
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setFocusPainted(false);
        fetchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        fetchButton.setPreferredSize(new Dimension(120, 30));

        nextStepButton = new JButton("Next Step");
        nextStepButton.setBackground(new Color(80, 80, 80));
        nextStepButton.setForeground(Color.WHITE);
        nextStepButton.setFocusPainted(false);
        nextStepButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        nextStepButton.setPreferredSize(new Dimension(120, 30));
        nextStepButton.setEnabled(false);

        inputPanel.add(inputLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(pageInput);
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(fetchButton);
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(nextStepButton);
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(demoButton);

        helpButton.addActionListener(e -> {
            String message = """
        Virtual Memory Simulator Help:
        
        - TLB: Fast cache for recent page translations.
        - Page Table: Maps virtual pages to physical frames.
        - Physical Memory (RAM): Where pages are stored for fast access.
        - Disk: Stores all pages; accessed on page faults.
        - Page Fault: Occurs when a page is not in RAM.
        - Replacement Policy: Determines which page is removed when RAM is full.
        
        Tip: Enter a page number and see how it flows through TLB → Page Table → RAM/Disk.
        """;
            JOptionPane.showMessageDialog(null, message, "Memory Concepts", JOptionPane.INFORMATION_MESSAGE);
        });



        addressDecompositionPane = new JTextPane();
        addressDecompositionPane.setEditable(false);
        addressDecompositionPane.setPreferredSize(new Dimension(300, 40));
        addressDecompositionPane.setFont(new Font("Monospaced", Font.BOLD, 14));
        addressDecompositionPane.setBackground(new Color(0, 70, 0));
        addressDecompositionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 8, 5));

        try {
            StyledDocument doc = addressDecompositionPane.getStyledDocument();
            SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(defaultStyle, Color.WHITE);
            doc.insertString(doc.getLength(), "Page #: - | Offset #: -", defaultStyle);
        } catch (BadLocationException ex) {

        }

        inputPanel.add(addressDecompositionPane);



        JPanel memoryPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        memoryPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        memoryPanel.setBackground(new Color(245, 245, 250));

        tlbArea = createStyledTextPanel();
        ptArea = createStyledTextPanel();
        ramArea = createStyledTextPanel();
       addRamClickHandler();
        diskArea = createStyledTextPanel();

        memoryPanel.add(createMemoryPanel(tlbArea, "TLB"));
        memoryPanel.add(createMemoryPanel(ptArea, "Page Table"));
        memoryPanel.add(createMemoryPanel(ramArea, "Physical Memory (RAM)"));
        memoryPanel.add(createMemoryPanel(diskArea, "Disk"));

        JPanel mainContent = new JPanel(new BorderLayout(0, 10));
        mainContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContent.setBackground(new Color(245, 245, 250));
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(new Color(245, 245, 250));

        topSection.add(statusPanel, BorderLayout.NORTH);
        topSection.add(inputPanel, BorderLayout.CENTER);
        mainContent.add(topSection, BorderLayout.NORTH);
        mainContent.add(memoryPanel, BorderLayout.CENTER);
        frame.add(mainContent, BorderLayout.CENTER);


        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statsPanel.setBackground(new Color(245, 245, 250));

        hitMissLabel = new JLabel("Hits: 0 | Misses: 0 | Hit Rate: 0%");
        hitMissLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        hitMissLabel.setForeground(new Color(30, 30, 30));

        statsPanel.add(hitMissLabel);
        topSection.add(statsPanel, BorderLayout.WEST);

        fetchButton.addActionListener(e -> fetchPage());
        nextStepButton.addActionListener(e -> nextStep());

        pageInput.requestFocusInWindow();
        MemoryStateSnapshot initialSnap = new MemoryStateSnapshot(
                "Initial State",
                tlb,
                pt,
                pm,
                "Simulation start"
        );

        frame.setVisible(true);
        updateDisplaysFromSnapshot(initialSnap, false);
    }

    void updateHitMissLabel() {
        int misses = no_queries - noHit;
        double hitRate = no_queries == 0 ? 0 : ((double) noHit / no_queries) * 100;
        hitMissLabel.setText(String.format("Hits: %d | Misses: %d | Hit Rate: %.2f%%", noHit, misses, hitRate));
    }

    private void addRamClickHandler() {
        ramArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int offset = ramArea.viewToModel2D(e.getPoint());
                if (offset < 0) return;

                try {
                    int lineIndex = ramArea.getDocument().getDefaultRootElement()
                            .getElementIndex(offset);
                    Element lineElem = ramArea.getDocument().getDefaultRootElement()
                            .getElement(lineIndex);
                    int start = lineElem.getStartOffset();
                    int end = lineElem.getEndOffset();
                    String currentLine = ramArea.getDocument().getText(start, end - start).trim();

                    JTextField inputField = new JTextField(currentLine, 20);
                    int result = JOptionPane.showConfirmDialog(
                            mainFrame,
                            inputField,
                            "Modify RAM line",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (result == JOptionPane.OK_OPTION) {
                        String newLine = inputField.getText().trim();
                        commitRamLineEdit(lineIndex, newLine);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    private void commitRamLineEdit(int lineIndex, String newLine) {
        try {
            String[] parts = newLine.split("\\|");
            if (parts.length >= 2) {
                int frameNum = Integer.parseInt(parts[0].replaceAll("\\D+", ""));
                String data = parts[1].replace("Data:", "").trim();

                pm.setFrameData(frameNum, data);

                Page p = pt.getPageFromFrame(frameNum);
                p.setDirty(true);
                MemoryStateSnapshot memoryModifiedSnap = new MemoryStateSnapshot(
                        "modified State",
                        tlb,
                        pt,
                        pm,
                        "simulation"
                );
                updateDisplaysFromSnapshot(memoryModifiedSnap, false);


            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showScenarioSelectionDialog() {
        String[] scenarios = {"Random", "Sequential"};
        JComboBox<String> scenarioCombo = new JComboBox<>(scenarios);

        JTextField startField = new JTextField("0", 10);
        JTextField countField = new JTextField("10", 10);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.add(new JLabel("Scenario Type:"));
        inputPanel.add(scenarioCombo);
        inputPanel.add(new JLabel("Start Page (sequential):"));
        inputPanel.add(startField);
        inputPanel.add(new JLabel("Count (sequential):"));
        inputPanel.add(countField);

        startField.setEnabled(false);
        countField.setEnabled(false);

        scenarioCombo.addActionListener(e -> {
            boolean isSequential = "Sequential".equals(scenarioCombo.getSelectedItem());
            startField.setEnabled(isSequential);
            countField.setEnabled(isSequential);
        });

        int result = JOptionPane.showConfirmDialog(
                mainFrame,
                inputPanel,
                "Select Demo Scenario",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String scenario = (String) scenarioCombo.getSelectedItem();

            if ("Random".equals(scenario)) {
                startAutomatedDemoWithScenario("random");
            } else if ("Sequential".equals(scenario)) {
                try {
                    int start = Integer.parseInt(startField.getText());
                    int count = Integer.parseInt(countField.getText());

                    if (count <= 0) {
                        MessageDispatcher.showWarning("Count must be positive.");
                        return;
                    }

                    List<Integer> demoSequence = DemoScenarios.sequentialScenario(start, count);
                    runDemoSequence(demoSequence, "sequential");

                } catch (NumberFormatException ex) {
                    MessageDispatcher.showWarning("Please enter valid numbers for start and count.");
                }
            }
        }
    }

    private void runDemoSequence(List<Integer> demoSequence, String scenarioName) {
        AutomatedDemoSimulator demo = new AutomatedDemoSimulator(this, demoSequence);
        demo.setDelayBetweenPages(2500);
        demo.setDelayBetweenSteps(800);

        JOptionPane.showMessageDialog(mainFrame,
                "Running " + scenarioName + " scenario\n" +
                        demoSequence.size() + " page accesses\n" +
                        "Observe the memory behavior!",
                "Automated Demo - " + scenarioName,
                JOptionPane.INFORMATION_MESSAGE);

        demo.startDemo();
    }
    public void updateAddressDecompositionDisplay(int virtualAddress, int pageSize) {int pageNumber = virtualAddress / pageSize;
        int offset = virtualAddress % offsetBits;

        StyledDocument doc = addressDecompositionPane.getStyledDocument();

        SimpleAttributeSet pageStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(pageStyle, new Color(135, 206, 250));
        StyleConstants.setBold(pageStyle, true);

        SimpleAttributeSet offsetStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(offsetStyle, new Color(255, 160, 122));
        StyleConstants.setBold(offsetStyle, true);

        SimpleAttributeSet generalStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(generalStyle, Color.WHITE);
        StyleConstants.setBold(generalStyle, true);

        try {
            doc.remove(0, doc.getLength());

            String binaryAddress = Integer.toBinaryString(virtualAddress);
            int fixedLength = 16;
            while (binaryAddress.length() < fixedLength) {
                binaryAddress = "0" + binaryAddress;
            }
            int calculatedOffsetBits = (int) (Math.log(pageSize) / Math.log(2));

            String pagePart = binaryAddress.substring(0, binaryAddress.length() - offsetBits);
            String offsetPart = binaryAddress.substring(binaryAddress.length() - offsetBits);

            doc.insertString(doc.getLength(), "P: ", generalStyle);
            doc.insertString(doc.getLength(), pagePart, pageStyle);
            doc.insertString(doc.getLength(), " | O: ", generalStyle);
            doc.insertString(doc.getLength(), offsetPart, offsetStyle);


        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }


    private void resetHighlightVariables() {
        this.frameFound = null;
        this.pageIntroduced = null;
    }

    private void clearHighlights() {
        resetHighlightVariables();

        updateDisplaysFromSnapshot(lastSnap, false);

    }
    void fetchPage() {
        try {
            int virtualAddress = Integer.parseInt(pageInput.getText());
            pageInput.setText("");
            int pageSize = 4;
            int pageNum = virtualAddress / pageSize;
            pageIntroduced = pageNum;
            int offset = virtualAddress % pageSize;
            updateAddressDecompositionDisplay(virtualAddress, pageSize);
            if (pageNum < 0) {
                MessageDispatcher.showWarning("Please enter a non-negative page .");
                return;
            }

            Page page = pt.getPage(pageNum);
            if (page == null) {
                MessageDispatcher.showError("Page " + pageNum + " does not exist.");
                return;
            }

            MemoryManager.snapshots.clear();
            StepByStepMessageDispatcher.clearAll();

            fetchButton.setEnabled(false);
            nextStepButton.setEnabled(true);

            no_queries++;
            boolean hit = mm.accessMemory(page, tlb, pt, pm, r, disk);
            if(hit)noHit++;

            StepByStepMessageDispatcher.addSnapshots(MemoryManager.snapshots);
            StepByStepMessageDispatcher.startStepByStep();
            updateHitMissLabel();


        } catch (NumberFormatException | InterruptedException ex) {
            MessageDispatcher.showWarning("Invalid input. Please enter a number.");
        }
    }

    void nextStep() {
        MemoryStateSnapshot snap = StepByStepMessageDispatcher.showNextSnapshot();
        if (snap != null) {
            updateDisplaysFromSnapshot(snap, true);
            lastSnap = snap;

        }

        if (snap == null) {
            clearHighlights();
            nextStepButton.setEnabled(false);
            fetchButton.setEnabled(true);
        }
    }

    private void updateDisplaysFromSnapshot(MemoryStateSnapshot snap, boolean highlight) {

        Object ptObj = snap.getPageTableState().get("entries");
        Object pmObj = snap.getPhysicalMemoryState().get("frames");
        Object tlbObj = snap.getTlbState().get("entries");
        Map<Integer, String> diskObj = disk.storage;
        if (ptObj instanceof Map) updatePageTableDisplayFromSnapshot((Map<Page, Integer>) ptObj);
        if (pmObj instanceof Map) updatePMDisplayFromSnapshot((Map<Integer, String>) pmObj, highlight);
        if (tlbObj instanceof Map) updateTLBDisplayFromSnapshot((Map<Integer, Integer>) tlbObj, highlight);
        if(diskObj != null){updateDiskDisplayFromSnapshot(diskObj, highlight);}
    }

    private String formatMap(Object obj) {
        if (!(obj instanceof Map)) return "[empty]";
        StringBuilder sb = new StringBuilder();
        Map<?, ?> map = (Map<?, ?>) obj;
        for (Map.Entry<?, ?> e : map.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue() == null ? "[empty]" : e.getValue()).append("\n");
        }
        return sb.toString();
    }


    private JPanel createMemoryPanel(JComponent component, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 120)),
                title, TitledBorder.CENTER, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)
        ));
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTextArea createTextPanel() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(250, 250, 255));
        area.setMargin(new Insets(5, 5, 5, 5));
        return area;
    }

    private JTextPane createStyledTextPanel() {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pane.setBackground(new Color(250, 250, 255));
        pane.setMargin(new Insets(5, 5, 5, 5));
        return pane;
    }

    private void updatePageTableDisplayFromSnapshot(Map<Page, Integer> ptMap) {
        StyledDocument doc = ptArea.getStyledDocument();
        SimpleAttributeSet validStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(validStyle, new Color(0, 100, 0));
        StyleConstants.setBackground(validStyle, new Color(220, 255, 220));
        StyleConstants.setBold(validStyle, true);
        SimpleAttributeSet invalidStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(invalidStyle, new Color(139, 0, 0));
        StyleConstants.setBackground(invalidStyle, new Color(255, 220, 220));
        StyleConstants.setBold(invalidStyle, true);



        try {
            doc.remove(0, doc.getLength());
            for (Map.Entry<Page, Integer> e : ptMap.entrySet()) {
                Page p = e.getKey();
                String line = "Page " + p.getP() + " | Frame: " + (e.getValue() == -1 ? "-" : e.getValue())
                        + " | Valid: " + (p.isValid() ? "1" : "0") + " | Dirty: " + (p.getDirty() ? "1" : "0") + "\n";
                doc.insertString(doc.getLength(), line, p.isValid() ? validStyle : invalidStyle);
            }
            ptArea.setCaretPosition(0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updatePMDisplayFromSnapshot(Map<Integer, String> ramMap, boolean hl) {
        StyledDocument doc = ramArea.getStyledDocument();
        SimpleAttributeSet validStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(validStyle, new Color(0, 0, 0));
        if(hl) StyleConstants.setBackground(validStyle, new Color(223, 255, 0));
        else  StyleConstants.setBackground(validStyle, new Color(255, 255, 255));
        StyleConstants.setBold(validStyle, true);
        SimpleAttributeSet invalidStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(invalidStyle, new Color(0, 0, 0));
        StyleConstants.setBackground(invalidStyle, new Color(255, 255, 255));
        StyleConstants.setBold(invalidStyle, true);



        try {
            doc.remove(0, doc.getLength());
            for (Map.Entry<Integer, String> e : ramMap.entrySet()) {
                Integer p = e.getKey();
                String line = "Frame " + p + " | Data: " +  e.getValue() + "\n";
                doc.insertString(doc.getLength(), line, (Objects.equals(p, frameFound)) ? validStyle : invalidStyle);
            }
            ramArea.setCaretPosition(0);

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }


    private void updateTLBDisplayFromSnapshot(Map<Integer, Integer> tlbMap, boolean hl) {
        StyledDocument doc = tlbArea.getStyledDocument();
        SimpleAttributeSet validStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(validStyle, new Color(0, 0, 0));
        if(hl) StyleConstants.setBackground(validStyle, new Color(223, 255, 0));
        else  StyleConstants.setBackground(validStyle, new Color(255, 255, 255));
        StyleConstants.setBold(validStyle, true);
        SimpleAttributeSet invalidStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(invalidStyle, new Color(0, 0, 0));
        StyleConstants.setBackground(invalidStyle, new Color(255, 255, 255));
        StyleConstants.setBold(invalidStyle, true);


        try {
            doc.remove(0, doc.getLength());
            for (Map.Entry<Integer, Integer> e : tlbMap.entrySet()) {
                Integer p = e.getKey();
                String line = "Page " + p + " | Frame: " + e.getValue() + "\n";
                doc.insertString(doc.getLength(), line, (Objects.equals(e.getValue(), frameFound)) ? validStyle : invalidStyle);
            }
            tlbArea.setCaretPosition(0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateDiskDisplayFromSnapshot(Map<Integer, String> diskMap, boolean hl) {
        StyledDocument doc = diskArea.getStyledDocument();
        SimpleAttributeSet validStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(validStyle, new Color(0, 0, 0));
        if(hl) StyleConstants.setBackground(validStyle, new Color(223, 255, 0));
        else  StyleConstants.setBackground(validStyle, new Color(255, 255, 255));
        StyleConstants.setBold(validStyle, true);
        SimpleAttributeSet invalidStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(invalidStyle, new Color(0, 0, 0));
        StyleConstants.setBackground(invalidStyle, new Color(255, 255, 255));
        StyleConstants.setBold(invalidStyle, true);


        try {
            doc.remove(0, doc.getLength());
            for (Map.Entry<Integer, String> e : diskMap.entrySet()) {
                Integer p = e.getKey();
                String line = "Page " + p + " | Data: " + e.getValue() + "\n";
                doc.insertString(doc.getLength(), line, (Objects.equals(p, pageIntroduced)) ? validStyle : invalidStyle);
            }
            diskArea.setCaretPosition(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startAutomatedDemoWithScenario(String scenarioName) {
        List<Integer> demoSequence;

        switch (scenarioName) {

            case "random":
                demoSequence = DemoScenarios.randomScenario(15);
                break;
            case "sequential":
                demoSequence = DemoScenarios.sequentialScenario(0, 10);
                break;
            default:
                demoSequence = DemoScenarios.randomScenario(5);
        }

        AutomatedDemoSimulator demo = new AutomatedDemoSimulator(this, demoSequence);
        demo.setDelayBetweenPages(2500);
        demo.setDelayBetweenSteps(800);

        JOptionPane.showMessageDialog(mainFrame,
                "Running " + scenarioName + " scenario\n" +
                        demoSequence.size() + " page accesses\n" +
                        "Observe the memory behavior!",
                "Automated Demo - " + scenarioName,
                JOptionPane.INFORMATION_MESSAGE);

        demo.startDemo();
    }
    private void showStatusMessage(String message) {
       SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }

    private void updateDisplays() {
        tlbArea.setText("");
        ramArea.setText("");
        diskArea.setText("");
        ptArea.setText("");
    }

    public static void main(String[] args) {
   SwingUtilities.invokeLater(() -> new SimulationConfigurator());
    }
}
