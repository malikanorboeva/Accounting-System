package ledger;

import exceptions.AccountException;

public abstract class Account {
	private String name;
	private boolean open = false;
	protected int value = 5000;
	protected int delta = 0;
	private Accountant lockHolder = null;

	public Account(String name) {
		this.name = name;
	}

	public abstract void credit(int value) throws AccountException;
	public abstract void debit(int value) throws AccountException;

	public synchronized void lock(Accountant accountant) {
		this.lockHolder = accountant;
		open = true;
	}

	public synchronized void unlock(Accountant accountant) {
		if (isLockedByAccountant(accountant)) {
			this.lockHolder = null;
			open = false;
		}
	}

	public synchronized boolean isLockedByAnotherAccountant(Accountant accountant) {
		return lockHolder != null && lockHolder != accountant;
	}

	public synchronized boolean isLockedByAccountant(Accountant accountant) {
		return lockHolder == accountant;
	}
	
	public void open() {
		this.open = true;
	}
	
	public void abort() {
		delta = 0;
	}
	
	public void commit() {
		value += delta;
		delta = 0;
	}
	
	public void close() {
		this.open = false;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {		
		return name + " (value=" + value + ", open=" + open + ")";
	}

}
