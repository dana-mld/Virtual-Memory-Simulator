package org.example.demo;

import org.example.view.MemorySimulatorGUI;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AutomatedDemoSimulator {
    private MemorySimulatorGUI gui;
    private List<Integer> pageSequence;
    private int currentIndex;
    private boolean isRunning;
    private ScheduledExecutorService scheduler;
    private int delayBetweenPages = 1000;
    private int delayBetweenSteps = 500;

    public AutomatedDemoSimulator(MemorySimulatorGUI gui, List<Integer> pageSequence) {
        this.gui = gui;
        this.pageSequence = pageSequence;
        this.currentIndex = 0;
        this.isRunning = false;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }


    public void startDemo() {
        if (isRunning) {
            return;
        }

        isRunning = true;

        scheduler.schedule(this::processNextPage, 500, TimeUnit.MILLISECONDS);
    }


    public void stopDemo() {
        isRunning = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }


    private void processNextPage() {
        if (!isRunning || currentIndex >= pageSequence.size()) {
            stopDemo();
            return;
        }

        int pageNumber = pageSequence.get(currentIndex);

        SwingUtilities.invokeLater(() -> {
            gui.pageInput.setText(String.valueOf(pageNumber));
            gui.fetchButton.doClick();

            scheduler.schedule(this::processNextStep, 500, TimeUnit.MILLISECONDS);
        });

        currentIndex++;
    }


    private void processNextStep() {
        SwingUtilities.invokeLater(() -> {
            if (gui.nextStepButton.isEnabled()) {
                gui.nextStepButton.doClick();

                scheduler.schedule(this::processNextStep, delayBetweenSteps, TimeUnit.MILLISECONDS);
            } else {
                scheduler.schedule(this::processNextPage, delayBetweenPages, TimeUnit.MILLISECONDS);
            }
        });
    }


    public void setDelayBetweenPages(int delayMs) {
        this.delayBetweenPages = delayMs;
    }


    public void setDelayBetweenSteps(int delayMs) {
        this.delayBetweenSteps = delayMs;
    }


    public boolean isRunning() {
        return isRunning;
    }


    public String getProgress() {
        return currentIndex + "/" + pageSequence.size();
    }
}

