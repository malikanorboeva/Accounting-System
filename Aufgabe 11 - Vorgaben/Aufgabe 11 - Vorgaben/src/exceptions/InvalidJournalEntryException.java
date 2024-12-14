package exceptions;

public class InvalidJournalEntryException extends Exception {
	
	private static final long serialVersionUID = 2396407396293703499L;

	public InvalidJournalEntryException(String message) {
		super(message);
	}

}
