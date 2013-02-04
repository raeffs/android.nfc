package ch.raphaelfleischlin.android.nfc;

import ch.hslu.pawi.h12.pizzatracker.android.OrderIdentifier;

/**
*
* @author Raphael Fleischlin <raphael.fleischlin@stud.hslu.ch>
*/
public interface NfcListener {

	void onWriteModeEnabled();
	
	void onWriteSuccessful();
	
	void onWriteFailed();
	
	void onNewData(OrderIdentifier data);
}
