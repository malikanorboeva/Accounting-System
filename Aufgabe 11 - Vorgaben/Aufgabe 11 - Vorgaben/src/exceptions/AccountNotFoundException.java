package exceptions;

public class AccountNotFoundException extends InvalidJournalEntryException {

	private static final long serialVersionUID = 7413627395359707237L;

	public AccountNotFoundException(String message) {
		super(message);
	}
}
