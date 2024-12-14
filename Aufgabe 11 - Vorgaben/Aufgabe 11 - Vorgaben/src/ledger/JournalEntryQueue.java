package ledger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class JournalEntryQueue {
    private final Queue<String> entries;
    private final int maxSize;
    private final AtomicInteger totalEntries;
    private final AtomicInteger processedEntries;

    public JournalEntryQueue(int maxSize) {
        this.entries = new LinkedList<>();
        this.maxSize = maxSize;
        this.totalEntries = new AtomicInteger(0);
        this.processedEntries = new AtomicInteger(0);
    }

    public synchronized boolean addEntry(String entry) {
        if (entries.size() < maxSize) {
            entries.add(entry);
            totalEntries.incrementAndGet();
            notify();
            return true;
        }
        return false;
    }

    public synchronized String removeFirstEntry() {
        while (entries.isEmpty()) {
            if (processedEntries.get() >= totalEntries.get()) {
                return null;
            }
            try {
                wait(100); // Wait with timeout
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        processedEntries.incrementAndGet();
        return entries.poll();
    }

    public synchronized int getCurrentSize() {
        return entries.size();
    }

    public synchronized boolean isEmpty() {
        return entries.isEmpty() && processedEntries.get() >= totalEntries.get();
    }
}