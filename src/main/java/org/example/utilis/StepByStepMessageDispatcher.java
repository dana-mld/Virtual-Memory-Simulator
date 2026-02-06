package org.example.utilis;

import org.example.view.MemoryStateSnapshot;

import java.util.*;
import java.util.function.Consumer;

public class StepByStepMessageDispatcher {

    private static Consumer<String> messageConsumer;
    private static LinkedList<MemoryStateSnapshot> snapshotQueue = new LinkedList<>();
    private static boolean isActive = false;

    public static void initialize(Consumer<String> consumer) {
        messageConsumer = consumer;
    }

    public static void addSnapshots(List<MemoryStateSnapshot> snapshots) {
        if (snapshots != null && !snapshots.isEmpty()) {
            snapshotQueue.addAll(snapshots);
        }
    }

    public static void startStepByStep() {
        isActive = true;
        showNextSnapshot();
    }

    public static MemoryStateSnapshot showNextSnapshot() {
        if (!isActive) return null;

        if (!snapshotQueue.isEmpty()) {
            MemoryStateSnapshot snap = snapshotQueue.removeFirst();

            if (messageConsumer != null) {
                messageConsumer.accept(snap.getMessage());
            }

            return snap;
        } else {
            if (messageConsumer != null) {
                messageConsumer.accept(" All steps completed!");
            }
            isActive = false;
            return null;
        }
    }

    public static boolean hasMoreSnapshots() {
        return !snapshotQueue.isEmpty();
    }

    public static void clearAll() {
        snapshotQueue.clear();
        isActive = false;
        if (messageConsumer != null) {
            messageConsumer.accept("Ready to begin");
        }
    }
}
