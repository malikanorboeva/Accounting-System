package main;

import exceptions.*;
import ledger.AccountManager;
import ledger.Accountant;
import ledger.JournalEntryQueue;

public class Main {

	public static void main(String[] args) throws Exception {
		AccountManager.init();

		// Create and populate the queue
		JournalEntryQueue entryQueue = new JournalEntryQueue();
		for (int i = 0; i < 100; i++) {
			String entry = AccountManager.getRandomJournalEntry(6);
			System.out.println("Generated entry: " + entry);
			entryQueue.addEntry(entry);
		}

		// Create and start accountant threads
		int numThreads = 5; // You can adjust this number
		Accountant[] accountants = new Accountant[numThreads];

		for (int i = 0; i < numThreads; i++) {
			accountants[i] = new Accountant(entryQueue);
			accountants[i].start();
		}

		// Wait for all accountants to finish
		for (Accountant accountant : accountants) {
			accountant.join();
		}

		AccountManager.printAccounts();
	}
}

