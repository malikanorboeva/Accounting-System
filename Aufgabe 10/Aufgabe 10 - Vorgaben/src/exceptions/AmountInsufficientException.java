package exceptions;

public class AmountInsufficientException extends AccountException {
	
	private static final long serialVersionUID = 60490504565097362L;

	public AmountInsufficientException(String message) {
		super(message);
	}

}
