package ledger;

import exceptions.AmountInsufficientException;
import exceptions.AccountClosedException;

public class Asset extends Account {

	public Asset(String name) {
		super(name);
	}

	@Override
	public void credit(int value) throws AccountClosedException, AmountInsufficientException {
		if (!isOpen()) throw new AccountClosedException("Asset account " + toString() + " closed.");

		if (this.value < value) throw new AmountInsufficientException("Amount insufficient on asset account " + toString());
		this.delta -= value;	
	}

	@Override
	public void debit(int value) throws AccountClosedException {
		if (!isOpen()) throw new AccountClosedException("Asset account " + toString() + " closed.");

		this.delta += value;
	}

}
