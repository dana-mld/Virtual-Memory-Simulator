package org.example.utilis;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageDispatcher {
    private static Consumer<String> messageConsumer;
    private static String currentMessageType = "info";
    private static final ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private static Timer messageTimer;
    private static boolean isProcessing = false;

    private static final int MESSAGE_DURATION = 1000;
    private static final int MESSAGE_TRANSITION = 500;

    private static class Message {
        String text;
        String type;

        Message(String text, String type) {
            this.text = text;
            this.type = type;
        }
    }

    public static void initialize(Consumer<String> consumer) {
        messageConsumer = consumer;
    }

    public static void showMessage(String message, String type) {
        messageQueue.offer(new Message(message, type));

        if (!isProcessing) {
            processNextMessage();
        }
    }

    private static void processNextMessage() {
        if (messageConsumer == null) return;

        Message nextMessage = messageQueue.poll();
        if (nextMessage != null) {
            isProcessing = true;

            currentMessageType = nextMessage.type;
            messageConsumer.accept(nextMessage.text);

            messageTimer = new Timer(MESSAGE_DURATION + MESSAGE_TRANSITION, e -> {
                if (!messageQueue.isEmpty()) {
                    processNextMessage();
                } else {
                    messageConsumer.accept("Virtual Memory Simulator");
                    currentMessageType = "info";
                    isProcessing = false;
                }
            });
            messageTimer.setRepeats(false);
            messageTimer.start();

        } else {
            isProcessing = false;
        }
    }

    public static void showHit(String message) {
        showMessage("✅ " + message, "hit");
    }

    public static void showPageFault(String message) {
        showMessage("📥 " + message, "pagefault");
    }

    public static void showMiss(String message) {
        showMessage("❌ " + message, "miss");
    }

    public static void showInfo(String message) {
        showMessage("ℹ️ " + message, "info");
    }

    public static void showWarning(String message) {
        showMessage("⚠️ " + message, "warning");
    }

    public static void showError(String message) {
        showMessage("🚫 " + message, "error");
    }

    public static String getCurrentMessageType() {
        return currentMessageType;
    }

    public static void clearAll() {
        if (messageTimer != null && messageTimer.isRunning()) {
            messageTimer.stop();
        }
        messageQueue.clear();
        isProcessing = false;
        if (messageConsumer != null) {
            messageConsumer.accept("Enter a page to start");
            currentMessageType = "info";
        }
    }

    public static boolean hasPendingMessages() {
        return !messageQueue.isEmpty() || isProcessing;
    }
}