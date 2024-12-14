package exceptions;

public class InvalidSyntaxException extends InvalidJournalEntryException {

	private static final long serialVersionUID = 8186114917120580086L;

	public InvalidSyntaxException(String message) {
		super(message);
	}
}
