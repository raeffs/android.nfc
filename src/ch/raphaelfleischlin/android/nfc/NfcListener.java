package ch.raphaelfleischlin.android.nfc;

/**
 *
 * @author Raphael Fleischlin <raphael.fleischlin at gmail.com>
 */
public interface NfcListener<T extends Payload> {

	void onWriteModeEnabled();
	
	void onWriteSuccessful();
	
	void onWriteFailed();
	
	void onNewData(T data);
}
