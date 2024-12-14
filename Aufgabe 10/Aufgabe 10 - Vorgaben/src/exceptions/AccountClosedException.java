package exceptions;

public class AccountClosedException extends AccountException {

	private static final long serialVersionUID = -5011280210129877931L;

	public AccountClosedException(String message) {
		super(message);
	}
}
