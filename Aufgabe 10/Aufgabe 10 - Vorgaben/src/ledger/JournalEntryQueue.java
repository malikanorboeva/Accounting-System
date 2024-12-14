package ledger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class JournalEntryQueue {
    private final Queue<String> entries = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public void addEntry(String entry) {
        lock.lock();
        try {
            entries.offer(entry);
        } finally {
            lock.unlock();
        }
    }

    public String removeEntry() {
        lock.lock();
        try {
            return entries.poll();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return entries.size();
        } finally {
            lock.unlock();
        }
    }
}