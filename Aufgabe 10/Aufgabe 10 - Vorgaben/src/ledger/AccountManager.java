package ledger;

import exceptions.AccountNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {
	private static final Map<String, Asset> assets = new ConcurrentHashMap<>();
	private static final Map<String, Liability> liabilities = new ConcurrentHashMap<>();
	private static final Random random = new Random();
	private static final int ERRORRATIO = 0;  // Set to 0 as requested in assignment

	public static Account getAccount(String name) throws AccountNotFoundException {
		Account account = assets.get(name);
		if (account != null) return account;

		account = liabilities.get(name);
		if (account != null) return account;

		throw new AccountNotFoundException("Account not found: " + name);
	}

	public static Account getAsset(String name) throws AccountNotFoundException {
		Account account = assets.get(name);
		if (account == null) {
			throw new AccountNotFoundException("Asset not found: " + name);
		}
		return account;
	}

	public static Account getLiability(String name) throws AccountNotFoundException {
		Account account = liabilities.get(name);
		if (account == null) {
			throw new AccountNotFoundException("Liability not found: " + name);
		}
		return account;
	}

	public static void init() {
		// Assets
		String[] assetNames = {
				"Cash", "Inventory", "Supplies", "Land", "Equipment", "Vehicles", "Buildings",
				"Operating_Revenues", "Other_Revenues"
		};
		for (String name : assetNames) {
			assets.put(name, new Asset(name));
		}

		// Liabilities
		String[] liabilityNames = {
				"Accounts_Payable", "Wages_Payable", "Interest_Payable", "Unearned_Payable",
				"Bonds_Payable", "Equity", "Cost_of_Goods_Sold", "Payroll_Expenses",
				"Marketing_Expenses", "Other_Expenses"
		};
		for (String name : liabilityNames) {
			liabilities.put(name, new Liability(name));
		}
	}

	public static void printAccounts() {
		System.out.println("\nAssets:");
		assets.values().forEach(asset -> System.out.println(" - " + asset.toString()));
		System.out.println("\nLiabilities:");
		liabilities.values().forEach(liability -> System.out.println(" - " + liability.toString()));
		System.out.println();
	}

	public static String getRandomJournalEntry() {
		return getRandomJournalEntry(random.nextInt(9) + 1);
	}

	public static String getRandomJournalEntry(int numberOfAffectedAccounts) {
		int debitAccounts = random.nextInt(numberOfAffectedAccounts - 1) + 1;
		int creditAccounts = numberOfAffectedAccounts - debitAccounts;
		int totalValue = 0;
		StringBuilder entry = new StringBuilder();

		// Generate debit entries
		for (int i = 0; i < debitAccounts; i++) {
			Account account = getRandomAccount();
			int value = random.nextInt(1000) + 1;
			totalValue += value;
			entry.append(account.getName()).append(" ").append(value);
			if (i < debitAccounts - 1) entry.append(", ");
		}

		entry.append("; ");

		// Generate credit entries
		int remainingValue = totalValue;
		for (int i = 0; i < creditAccounts; i++) {
			Account account = getRandomAccount();
			int value = (i == creditAccounts - 1) ?
					remainingValue :
					random.nextInt(remainingValue - (creditAccounts - i - 1)) + 1;
			remainingValue -= value;
			entry.append(account.getName()).append(" ").append(value);
			if (i < creditAccounts - 1) entry.append(", ");
		}

		return entry.toString();
	}

	private static Account getRandomAccount() {
		List<Account> allAccounts = new ArrayList<>();
		allAccounts.addAll(assets.values());
		allAccounts.addAll(liabilities.values());
		return allAccounts.get(random.nextInt(allAccounts.size()));
	}
}