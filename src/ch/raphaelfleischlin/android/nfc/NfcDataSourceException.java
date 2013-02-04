package ch.raphaelfleischlin.android.nfc;

/**
*
* @author Raphael Fleischlin <raphael.fleischlin@stud.hslu.ch>
*/
public class NfcDataSourceException extends Exception {
	
	private static final long serialVersionUID = -5613866727954867390L;

	public NfcDataSourceException(String error) {
		super(error);
	}
	
	public NfcDataSourceException(String error, Throwable throwable) {
		super(error, throwable);
	}

}
