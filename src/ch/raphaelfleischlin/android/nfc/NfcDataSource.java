package ch.raphaelfleischlin.android.nfc;

import java.io.IOException;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

/**
 *
 * @author Raphael Fleischlin <raphael.fleischlin at gmail.com>
 */
public class NfcDataSource {
	
	private static final String LOGTAG = "NfcDataSource";
	
	private Tag underlayingTag;
	
	public NfcDataSource(Tag nfcTag) {
		underlayingTag = nfcTag;
	}

	public void write(NdefMessage message) throws Exception {
		if (isTagFormatted()) {
			writeTag(message);
		} else if (isTagFormatable()) {
			formatTag(message);
		} else {
			String error = "NFC tag is not supported!";
			Log.e(LOGTAG, error);
			throw new Exception(error);
		}
	}
	
	private boolean isTagFormatted() {
		return (Ndef.get(underlayingTag) != null);
	}
	
	private void writeTag(NdefMessage message) throws Exception {
		try {
			Ndef tagWriter = Ndef.get(underlayingTag);
			tagWriter.connect();
			checkIfWritable(tagWriter);
			checkSize(tagWriter, message);
			tagWriter.writeNdefMessage(message);
		} catch (IOException e) {
			String error = "Could not write to NFC tag!";
			Log.e(LOGTAG, error, e);
			throw new Exception(error, e);
		} catch (FormatException e) {
			String error = "Could not write to NFC tag due to bad formatted message!";
			Log.e(LOGTAG, error, e);
			throw new Exception(error, e);
		}
	}
	
	private void checkIfWritable(Ndef tagWriter) throws Exception {
		if (!tagWriter.isWritable()) {
			String error = "NFC tag is readonly!";
			Log.e(LOGTAG, error);
			throw new Exception(error);
		}
	}
	
	private void checkSize(Ndef tagWriter, NdefMessage message) throws Exception  {
		int messageSize = message.getByteArrayLength();
		int maxSize = tagWriter.getMaxSize();
		if (messageSize > maxSize) {
			String error = String.format("NFC tag has not enough free space (free: %d, required: %d)!", maxSize, messageSize);
			Log.e(LOGTAG, error);
			throw new Exception(error);
		}
	}
	
	private boolean isTagFormatable() {
		return (NdefFormatable.get(underlayingTag) != null);
	}
	
	private void formatTag(NdefMessage message) throws Exception {
		try {
			NdefFormatable tagFormatter = NdefFormatable.get(underlayingTag);
			tagFormatter.connect();
			tagFormatter.format(message);
		} catch (IOException e) {
			String error = "Could not format NFC tag!";
			Log.e(LOGTAG, error, e);
			throw new Exception(error, e);
		} catch (FormatException e) {
			String error = "Could not format NFC tag due to bad formatted message!";
			Log.e(LOGTAG, error, e);
			throw new Exception(error, e);
		}
	}
	
	public NdefMessage read() {
		if (isTagFormatted()) {
			return readFromTag();
		}
		return null;
	}
	
	private NdefMessage readFromTag() {
		Ndef tagReader = Ndef.get(underlayingTag);
		return tagReader.getCachedNdefMessage();
	}
}
