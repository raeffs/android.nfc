package ch.raphaelfleischlin.android.nfc;

import ch.hslu.pawi.h12.pizzatracker.android.OrderIdentifier;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
*
* @author Raphael Fleischlin <raphael.fleischlin@stud.hslu.ch>
*/
public class PayloadMapper {
	
	public NdefMessage mapToMessage(OrderIdentifier payload) {
		NdefRecord record = new NdefRecord(
				NdefRecord.TNF_MIME_MEDIA,
				OrderIdentifier.MIME_TYPE.getBytes(),
				new byte[] {},
				payload.toString().getBytes());
		NdefMessage message = new NdefMessage(record);
		return message;
	}
	
	public OrderIdentifier mapToPayload(NdefMessage message) {
		NdefRecord record = message.getRecords()[0];
		OrderIdentifier payload = null;
		String mimeType = new String(record.getType());
		if (NdefRecord.TNF_MIME_MEDIA == record.getTnf()
				&& OrderIdentifier.MIME_TYPE.equals(mimeType)) {
			String rawData = new String(record.getPayload());
			payload = new OrderIdentifier();
			payload.fromString(rawData);
		}
		return payload;
	}

}
