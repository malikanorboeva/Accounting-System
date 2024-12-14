package ledger;

import java.util.*;
import exceptions.*;
import util.Pair;

public class Accountant implements Runnable {
	private final JournalEntryQueue queue;
	private final Set<Account> lockedAccounts;
	private int successfulPostings;
	private volatile boolean isRunning;

	public Accountant(JournalEntryQueue queue) {
		this.queue = queue;
		this.lockedAccounts = new HashSet<>();
		this.successfulPostings = 0;
		this.isRunning = true;
	}

	@Override
	public void run() {
		while (isRunning) {
			String entry = queue.removeFirstEntry();
			if (entry == null) {
				isRunning = false;
				break;
			}

			try {
				postEntry(entry);
				successfulPostings++;
			} catch (InvalidJournalEntryException e) {
				// Discard entry with syntax error
			} catch (AccountException e) {
				// If account is locked by another accountant, put entry back in queue
				if (e instanceof AccountClosedException) {
					queue.addEntry(entry);
				}
				// Other account exceptions mean entry should be discarded
			} finally {
				releaseAllLocks();
			}
		}
	}

	private boolean tryLockAccount(Account account) {
		synchronized (account) {
			if (!account.isLockedByAnotherAccountant(this)) {
				account.lock(this);
				lockedAccounts.add(account);
				return true;
			}
			return false;
		}
	}

	private void releaseAllLocks() {
		for (Account account : lockedAccounts) {
			synchronized (account) {
				account.unlock(this);
			}
		}
		lockedAccounts.clear();
	}

	public int getSuccessfulPostings() {
		return successfulPostings;
	}

	public void postEntry(String journalEntry) throws InvalidJournalEntryException, AccountException {
		// Parse and validate entry
		if (journalEntry.indexOf(";") == -1)
			throw new InvalidSyntaxException("Invalid syntax: ; missing in " + journalEntry);

		String[] debitCredit = journalEntry.split(";");
		if (debitCredit.length != 2)
			throw new InvalidSyntaxException("Invalid syntax: ; must split entry into two parts: " + journalEntry);

		List<Pair<Account, Integer>> debits = parseJournalEntry(debitCredit[0]);
		List<Pair<Account, Integer>> credits = parseJournalEntry(debitCredit[1]);

		// Verify balance
		verifyBalance(debits, credits, journalEntry);

		// Try to acquire all locks first to prevent deadlocks
		if (!tryAcquireAllLocks(debits, credits)) {
			throw new AccountClosedException("Could not acquire all necessary locks");
		}

		// Process the entry
		try {
			processEntries(debits, credits);
		} catch (AccountException e) {
			abortTransaction(debits, credits);
			throw e;
		}
	}

	private boolean tryAcquireAllLocks(List<Pair<Account, Integer>> debits, List<Pair<Account, Integer>> credits) {
		// Sort accounts by hashCode to prevent deadlocks
		Set<Account> accounts = new TreeSet<>((a1, a2) ->
				Integer.compare(System.identityHashCode(a1), System.identityHashCode(a2)));

		debits.forEach(p -> accounts.add(p.getFirst()));
		credits.forEach(p -> accounts.add(p.getFirst()));

		// Try to acquire all locks in order
		for (Account account : accounts) {
			if (!tryLockAccount(account)) {
				releaseAllLocks();
				return false;
			}
		}
		return true;
	}

	private void verifyBalance(List<Pair<Account, Integer>> debits, List<Pair<Account, Integer>> credits, String journalEntry) throws UnequalBalanceException {
		int balance = 0;
		for (Pair<Account, Integer> p : debits) {
			balance += p.getSecond();
		}
		for (Pair<Account, Integer> p : credits) {
			balance -= p.getSecond();
		}
		if (balance != 0) {
			throw new UnequalBalanceException("Unequal balance in: " + journalEntry);
		}
	}

	private void processEntries(List<Pair<Account, Integer>> debits, List<Pair<Account, Integer>> credits) throws AccountException {
		// Apply debits
		for (Pair<Account, Integer> p : debits) {
			p.getFirst().debit(p.getSecond());
		}

		// Apply credits
		for (Pair<Account, Integer> p : credits) {
			p.getFirst().credit(p.getSecond());
		}

		// If all operations successful, commit the changes
		commitTransaction(debits);
		commitTransaction(credits);
	}

	private void abortTransaction(List<Pair<Account, Integer>> debits, List<Pair<Account, Integer>> credits) {
		// Abort changes for debit accounts
		for (Pair<Account, Integer> p : debits) {
			p.getFirst().abort();
		}

		// Abort changes for credit accounts
		for (Pair<Account, Integer> p : credits) {
			p.getFirst().abort();
		}
	}

	private void commitTransaction(List<Pair<Account, Integer>> accounts) {
		for (Pair<Account, Integer> p : accounts) {
			p.getFirst().commit();
		}
	}
	
	private List<Pair<Account, Integer>> parseJournalEntry(String debitOrCreditPart) throws InvalidSyntaxException, AccountNotFoundException {
		
		List<Pair<Account, Integer>> accountsAndValues = new ArrayList<>();
		
		for (String accountAndValue : debitOrCreditPart.split(",")) {
			accountAndValue = accountAndValue.trim();
			if (accountAndValue.indexOf(" ") == -1) { 
				throw new InvalidSyntaxException("Invalid syntax: account name and value must be splitted by a space in " + accountAndValue);
			}
			String[] parts = accountAndValue.split(" ");
			if (parts.length != 2) {
				throw new InvalidSyntaxException("Invalid syntax: spaces are not allowed in account names: " + accountAndValue);
			}
			String accountName = parts[0];
			try {
				Account account = AccountManager.getAccount(accountName);
				Integer value = Integer.parseInt(parts[1]);				
				accountsAndValues.add(new Pair<>(account, value));
			} catch (AccountNotFoundException e) {
				throw e;
			} catch (NumberFormatException e) {
				throw new InvalidSyntaxException("Invalid syntax: given value is not an integer number: " + parts[1]);
			}			
		}
		return accountsAndValues;
	}

}
