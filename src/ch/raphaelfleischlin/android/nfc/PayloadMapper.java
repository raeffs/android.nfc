package ch.raphaelfleischlin.android.nfc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;

/**
 *
 * @author Raphael Fleischlin <raphael.fleischlin at gmail.com>
 */
public class PayloadMapper<T extends Payload> {
	
	private static final String LOGTAG = "PayloadMapper";
	
	private Class<T> clazz;
	
	public PayloadMapper(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public NdefMessage mapToMessage(T payload) throws Exception {
		NdefRecord record = new NdefRecord(
				NdefRecord.TNF_MIME_MEDIA,
				getMimeType().getBytes(),
				new byte[] {},
				marshalPayload(payload));
		NdefMessage message = new NdefMessage(record);
		return message;
	}
	
	private byte[] marshalPayload(T payload) throws Exception {
		ObjectOutputStream objectStream = null;
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(payload);
			return byteStream.toByteArray();
		} catch (IOException exception) {
			String error = "Could not marshal payload!";
			Log.e(LOGTAG, error, exception);
			throw new Exception(error, exception);
		} finally {
			closeResourceSilently(objectStream);
		}
	}
	
	public T mapToPayload(NdefMessage message) throws Exception {
		NdefRecord record = message.getRecords()[0];
		T payload = null;
		if (NdefRecord.TNF_MIME_MEDIA == record.getTnf()
				&& getMimeType().equals(new String(record.getType()))) {
			payload = unmarshalPayload(record.getPayload());
		}
		return payload;
	}
	
	@SuppressWarnings("unchecked")
	private T unmarshalPayload(byte[] data) throws Exception {
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new ByteArrayInputStream(data));
			return (T)stream.readObject();
		} catch (IOException exception) {
			String error = "Could not unmarshal payload!";
			Log.e(LOGTAG, error, exception);
			throw new Exception(error, exception);
		} catch (ClassNotFoundException exception) {
			String error = "Could not unmarshal payload because the class was not found!";
			Log.e(LOGTAG, error, exception);
			throw new Exception(error, exception);
		} finally {
			closeResourceSilently(stream);
		}
	}
	
	private void closeResourceSilently(Closeable resource) {
		try {
			resource.close();
		} catch (IOException exception) {
			Log.e(LOGTAG, "Exception while closing the resource!", exception);
		}
	}
	
	public String getMimeType() {
		return String.format("application/%s", clazz.getName());
	}

}
