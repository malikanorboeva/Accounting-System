package exceptions;

public class UnequalBalanceException extends InvalidJournalEntryException{
	
	private static final long serialVersionUID = -9131093352497607452L;

	public UnequalBalanceException(String message) {
		super(message);
	}

}
