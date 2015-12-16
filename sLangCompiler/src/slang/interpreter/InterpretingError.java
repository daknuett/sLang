package slang.interpreter;

public class InterpretingError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InterpretingError() {
	}

	public InterpretingError(String message) {
		super(message);
	}

	public InterpretingError(Throwable cause) {
		super(cause);
	}

	public InterpretingError(String message, Throwable cause) {
		super(message, cause);
	}

	public InterpretingError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
