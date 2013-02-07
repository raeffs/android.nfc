package ch.raphaelfleischlin.android.nfc;

/**
 *
 * @author Raphael Fleischlin <raphael.fleischlin at gmail.com>
 */
public class Exception extends java.lang.Exception {
	
	private static final long serialVersionUID = -5613866727954867390L;

	public Exception(String error) {
		super(error);
	}
	
	public Exception(String error, Throwable throwable) {
		super(error, throwable);
	}

}
