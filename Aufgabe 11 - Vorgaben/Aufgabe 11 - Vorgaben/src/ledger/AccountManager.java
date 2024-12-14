package ledger;

import java.util.ArrayList;
import java.util.Random;

import exceptions.AccountNotFoundException;

public class AccountManager {
	
	private static ArrayList<Asset> assets = new ArrayList<>();
	private static ArrayList<Liability> liabilities = new ArrayList<>();
	private static Random r = new Random();
	
	private static final int ERRORRATIO = 0;
	
	public static Account getAccount(String name) throws AccountNotFoundException {
		try {
			return getAsset(name);
		} catch (AccountNotFoundException e) {
			return getLiability(name);
		}
	}
	
	public static Account getAsset(String name) throws AccountNotFoundException {
		for(Account a : assets) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		throw new AccountNotFoundException("Unable to find account " + name);
	}
	
	public static Account getLiability(String name) throws AccountNotFoundException {
		for(Account a : liabilities) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		throw new AccountNotFoundException("Unable to find account " + name);
	}	
	
	public static void printAccounts() {
		System.out.println("Assets:");
		for (Account a : assets) {
			System.out.println(" - " + a.toString());
		}
		System.out.println("Liabilities:");
		for (Account a : liabilities) {
			System.out.println(" - " + a.toString());
		}
	}
	
	public static void init() {
		//Debits
		assets.add(new Asset("Cash"));
		assets.add(new Asset("Inventory"));
		assets.add(new Asset("Supplies"));
		assets.add(new Asset("Land"));
		assets.add(new Asset("Equipment"));
		assets.add(new Asset("Vehicles"));
		assets.add(new Asset("Buildings"));
		//Revenues/Gains
		assets.add(new Asset("Operating_Revenues"));
		assets.add(new Asset("Other_Revenues"));		
		
		//Credits
		liabilities.add(new Liability("Accounts_Payable"));
		liabilities.add(new Liability("Wages_Payable"));
		liabilities.add(new Liability("Interest_Payable"));
		liabilities.add(new Liability("Unearned_Payable"));
		liabilities.add(new Liability("Bonds_Payable"));
		liabilities.add(new Liability("Accounts_Payable"));
		liabilities.add(new Liability("Equity"));
		//Expenses/Losses
		liabilities.add(new Liability("Cost_of_Goods_Sold"));
		liabilities.add(new Liability("Payroll_Expenses"));
		liabilities.add(new Liability("Marketing_Expenses"));
		liabilities.add(new Liability("Other_Expenses"));		
	}

	
	public static String getRandomJournalEntry() {
		return getRandomJournalEntry(r.nextInt(9)+1);
	}
	
	public static String getRandomJournalEntry(int numberOfAffectedAcounts) {
		int d = r.nextInt(numberOfAffectedAcounts-1) + 1;
		int c = numberOfAffectedAcounts-d;
		
		int value = 0;
		String entry = "";
		
		for(int i = 0; i < d; i++) {
			Account a = getRandomAccount();
			int v = r.nextInt(1000)+1;
			
			
			if (r.nextInt(100) < ERRORRATIO) {
				value += v;
				if (r.nextInt(100) < 50) {
					entry += a.getName() + " " + v + "* ";
				} else {
					entry += a.getName() + "_NEW " + v + ", ";
				}
			} else {
				value += v;
				entry += a.getName() + " " + v + ", ";
			}
		}
		entry = entry.substring(0, entry.length()-2);
		
		if (r.nextInt(100) < ERRORRATIO) {
			if (r.nextInt(100) < 50) {
				entry += "# ";
			} else {
				entry += " ";
			}
		} else {
			entry += "; ";
		}		
		
		for(int i = 0; i < c - 1; i++) {
			Account a = getRandomAccount();
			int v = value / 3;
			
			value -= v;			
			entry += a.getName() + " " + v + ", ";
		}
		
		Account a = getRandomAccount();
		int v = value;
		
		if (r.nextInt(100) < ERRORRATIO) {
			v++;
			entry += a.getName() + " " + v;
		} else {
			entry += a.getName() + " " + v;
		}
		
		return entry;
	}
	
	private static Account getRandomAccount() {
		int i = r.nextInt(assets.size() + liabilities.size());
		if (i < assets.size()) {
			return assets.get(i);
		}
		return liabilities.get(i-assets.size());
	}

}
