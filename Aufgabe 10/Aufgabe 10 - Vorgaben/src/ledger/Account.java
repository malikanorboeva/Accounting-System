package ledger;

public abstract class Account {
	private final String name;
	private boolean open;
	protected int value;
	protected int delta;
	private Accountant lockHolder;

	public Account(String name, int initialValue) {
		this.name = name;
		this.value = initialValue;
		this.open = false;
		this.delta = 0;
	}

	public abstract void credit(int value) throws Exception;
	public abstract void debit(int value) throws Exception;

	public boolean open(Accountant accountant) {
		if (this.lockHolder == null && !this.open) {
			this.open = true;
			this.lockHolder = accountant;
			return true;
		}
		return false;
	}

	public boolean isLockedByAccountant(Accountant accountant) {
		return this.lockHolder == accountant;
	}

	public void abort(Accountant accountant) {
		if (isLockedByAccountant(accountant)) {
			delta = 0;
		}
	}

	public boolean commit(Accountant accountant) throws Exception {
		if (!isLockedByAccountant(accountant)) {
			return false;
		}
		value += delta;
		delta = 0;
		return true;
	}

	public boolean close(Accountant accountant) {
		if (isLockedByAccountant(accountant)) {
			this.open = false;
			this.lockHolder = null;
			return true;
		}
		return false;
	}

	public boolean isOpen() {
		return open;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("%s (value=%d, open=%b)", name, value, open);
	}
}
