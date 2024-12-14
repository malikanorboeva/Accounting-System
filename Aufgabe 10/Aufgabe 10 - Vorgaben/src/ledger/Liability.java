package ledger;

import exceptions.AccountClosedException;
import exceptions.AmountInsufficientException;

public class Liability extends Account {

	public Liability(String name) {
		super(name, 15000);  // Default initial value
	}

	public Liability(String name, int initialValue) {
		super(name, initialValue);
	}

	@Override
	public void credit(int value) throws Exception {
		if (!isOpen()) {
			throw new AccountClosedException("Cannot credit closed liability account: " + getName());
		}
		if (value < 0) {
			throw new IllegalArgumentException("Credit value cannot be negative");
		}
		this.delta += value;
	}

	@Override
	public void debit(int value) throws Exception {
		if (!isOpen()) {
			throw new AccountClosedException("Cannot debit closed liability account: " + getName());
		}
		if (value < 0) {
			throw new IllegalArgumentException("Debit value cannot be negative");
		}
		this.delta -= value;
	}

	@Override
	public boolean commit(Accountant accountant) throws Exception {
		if (!isLockedByAccountant(accountant)) {
			return false;
		}
		if (value + delta < 0) {
			throw new AmountInsufficientException("Insufficient balance in liability account: " + getName());
		}
		return super.commit(accountant);
	}
}

