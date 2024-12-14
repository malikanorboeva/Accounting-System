package main;

import ledger.AccountManager;
import ledger.Accountant;
import ledger.JournalEntryQueue;

public class Main {
	public static void main(String[] args) {
		AccountManager.init();

		// Test configurations
		int[] entryCounts = {10000, 100000, 1000000};
		int[] threadCounts = {1, 2, 5, 10};

		// Run tests for each configuration
		for (int entryCount : entryCounts) {
			System.out.println("\nTesting with " + entryCount + " entries:");

			for (int threadCount : threadCounts) {
				System.out.println("\nStarting test with " + threadCount + " threads...");
				runTest(entryCount, threadCount);
			}
		}
	}

	private static void runTest(int entryCount, int threadCount) {
		// Initialize queue and fill with random entries
		JournalEntryQueue queue = new JournalEntryQueue(entryCount);

		System.out.println("Generating " + entryCount + " random entries...");
		for (int i = 0; i < entryCount; i++) {
			queue.addEntry(AccountManager.getRandomJournalEntry(4));
		}

		System.out.println("Processing entries...");

		// Create and start accountants
		Accountant[] accountants = new Accountant[threadCount];
		Thread[] threads = new Thread[threadCount];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < threadCount; i++) {
			accountants[i] = new Accountant(queue);
			threads[i] = new Thread(accountants[i], "Accountant-" + i);
			threads[i].start();
		}

		// Wait for all threads to complete
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		long duration = System.currentTimeMillis() - startTime;

		// Calculate total successful postings
		int totalPostings = 0;
		for (Accountant accountant : accountants) {
			totalPostings += accountant.getSuccessfulPostings();
		}

		System.out.printf("%d threads: %d successful postings in %dms%n",
				threadCount, totalPostings, duration);
	}
}
