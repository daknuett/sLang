package slang.interpreter;

public class InterpretingError extends Exception {

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
