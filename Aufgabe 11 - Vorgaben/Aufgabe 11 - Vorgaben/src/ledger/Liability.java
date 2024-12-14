package ledger;

import exceptions.AmountInsufficientException;
import exceptions.AccountClosedException;

public class Liability extends Account {

	public Liability(String name) {
		super(name);
	}

	@Override
	public void credit(int value) throws AccountClosedException {
		if (!isOpen()) throw new AccountClosedException("Liability account " + toString() + " closed.");

		this.delta += value;	
	}

	@Override
	public void debit(int value) throws AccountClosedException, AmountInsufficientException {
		if (!isOpen()) throw new AccountClosedException("Liability account " + toString() + " closed.");

		if (this.value < value) throw new AmountInsufficientException("Amount insufficient on liability account " + toString());
			
		this.delta -= value;
	}

}
