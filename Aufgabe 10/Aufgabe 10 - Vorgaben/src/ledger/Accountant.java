package ledger;

import exceptions.*;
import util.Pair;

import javax.security.auth.login.AccountLockedException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Accountant extends Thread {
	private final JournalEntryQueue entryQueue;
	private final Set<String> lockedAccounts = new HashSet<>();
	private final Lock lock = new ReentrantLock();
	private static final int MAX_RETRIES = 3;
	private static final long DEADLOCK_TIMEOUT = 1000; // 1 second

	public Accountant(JournalEntryQueue queue) {
		this.entryQueue = queue;
	}

	@Override
	public void run() {
		while (true) {
			String entry = entryQueue.removeEntry();
			if (entry == null) {
				break; // No more entries to process
			}

			try {
				processEntry(entry);
			} catch (Exception e) {
				// Log error but continue processing other entries
				System.err.println("Error processing entry: " + e.getMessage());
			}
		}
	}

	private void processEntry(String entry) throws Exception {
		int retries = 0;
		while (retries < MAX_RETRIES) {
			try {
				postEntry(entry);
				return;
			} catch (AccountLockedException | AccountClosedException e) {
				retries++;
				if (retries < MAX_RETRIES) {
					// Wait before retry to avoid contention
					Thread.sleep(100 * retries);
				} else {
					// Put entry back in queue for later processing
					entryQueue.addEntry(entry);
				}
			} catch (Exception e) {
				// Other errors - don't retry
				throw e;
			}
		}
	}

	private void postEntry(String journalEntry) throws Exception {
		// Input validation
		if (journalEntry == null || journalEntry.trim().isEmpty()) {
			throw new InvalidJournalEntryException("Empty journal entry");
		}

		// Parse and validate entry format
		String[] parts = journalEntry.trim().split(";");
		if (parts.length != 2) {
			throw new InvalidSyntaxException("Invalid entry format: must contain exactly one semicolon");
		}

		// Parse debit and credit entries
		List<Pair<Account, Integer>> debitPairs = parseEntries(parts[0].trim());
		List<Pair<Account, Integer>> creditPairs = parseEntries(parts[1].trim());

		// Validate balanced transaction
		int debitTotal = calculateTotal(debitPairs);
		int creditTotal = calculateTotal(creditPairs);
		if (debitTotal != creditTotal) {
			throw new UnequalBalanceException(
					String.format("Unequal totals: debit=%d, credit=%d", debitTotal, creditTotal));
		}

		// Collect all accounts involved
		Set<Account> accounts = new HashSet<>();
		debitPairs.forEach(pair -> accounts.add(pair.getFirst()));
		creditPairs.forEach(pair -> accounts.add(pair.getFirst()));

		// Sort accounts to prevent deadlocks
		List<Account> sortedAccounts = new ArrayList<>(accounts);
		sortedAccounts.sort(Comparator.comparing(Account::getName));

		// Try to acquire all locks
		boolean locksAcquired = acquireAccountLocks(sortedAccounts);
		if (!locksAcquired) {
			throw new AccountLockedException("Could not acquire all necessary account locks");
		}

		try {
			// Open all accounts
			for (Account account : sortedAccounts) {
				if (!account.open(this)) {
					throw new AccountLockedException("Could not open account: " + account.getName());
				}
			}

			// Apply debits and credits
			for (Pair<Account, Integer> pair : debitPairs) {
				pair.getFirst().debit(pair.getSecond());
			}
			for (Pair<Account, Integer> pair : creditPairs) {
				pair.getFirst().credit(pair.getSecond());
			}

			// Commit all changes
			for (Account account : sortedAccounts) {
				if (!account.commit(this)) {
					throw new AccountException("Failed to commit changes to account: " + account.getName());
				}
			}
		} catch (Exception e) {
			// Rollback on any error
			for (Account account : sortedAccounts) {
				try {
					account.abort(this);
				} catch (Exception rollbackError) {
					// Log rollback error but continue with other rollbacks
					System.err.println("Error during rollback: " + rollbackError.getMessage());
				}
			}
			throw e;
		} finally {
			// Always close accounts and release locks
			for (Account account : sortedAccounts) {
				try {
					account.close(this);
				} finally {
					releaseAccountLock(account.getName());
				}
			}
		}
	}

	private List<Pair<Account, Integer>> parseEntries(String entriesStr) throws Exception {
		List<Pair<Account, Integer>> pairs = new ArrayList<>();
		if (entriesStr.isEmpty()) {
			return pairs;
		}

		String[] entries = entriesStr.split(",");
		for (String entry : entries) {
			entry = entry.trim();
			String[] parts = entry.split("\\s+");
			if (parts.length != 2) {
				throw new InvalidSyntaxException("Invalid entry format: " + entry);
			}

			String accountName = parts[0];
			int amount;
			try {
				amount = Integer.parseInt(parts[1]);
				if (amount <= 0) {
					throw new InvalidSyntaxException("Amount must be positive: " + amount);
				}
			} catch (NumberFormatException e) {
				throw new InvalidSyntaxException("Invalid amount: " + parts[1]);
			}

			Account account = AccountManager.getAccount(accountName);
			pairs.add(new Pair<>(account, amount));
		}
		return pairs;
	}

	private int calculateTotal(List<Pair<Account, Integer>> pairs) {
		return pairs.stream()
				.mapToInt(Pair::getSecond)
				.sum();
	}

	private boolean acquireAccountLock(String accountName) {
		lock.lock();
		try {
			if (lockedAccounts.contains(accountName)) {
				return false;
			}
			lockedAccounts.add(accountName);
			return true;
		} finally {
			lock.unlock();
		}
	}

	private void releaseAccountLock(String accountName) {
		lock.lock();
		try {
			lockedAccounts.remove(accountName);
		} finally {
			lock.unlock();
		}
	}

	private boolean acquireAccountLocks(List<Account> accounts) {
		lock.lock();
		try {
			for (Account account : accounts) {
				if (!acquireAccountLock(account.getName())) {
					// Release any locks we've already acquired
					accounts.stream()
							.map(Account::getName)
							.forEach(this::releaseAccountLock);
					return false;
				}
			}
			return true;
		} finally {
			lock.unlock();
		}
	}
}