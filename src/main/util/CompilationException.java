package main.util;

/**
 * Indicates that compilation failed
 */
public class CompilationException extends RuntimeException {
	private static final long serialVersionUID = 5272588827551900536L;

	/**
	 * Creates an exception with a message
	 * @param msg
	 *   Error message
	 */
	public CompilationException(String msg) {
		super(msg);
	}

}
