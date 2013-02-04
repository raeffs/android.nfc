package ch.raphaelfleischlin.android.nfc;

import ch.hslu.pawi.h12.pizzatracker.android.OrderIdentifier;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;

/**
*
* @author Raphael Fleischlin <raphael.fleischlin@stud.hslu.ch>
*/
public class NfcWrapper {

	private Activity context;
	private PayloadMapper payloadMapper = new PayloadMapper();
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private OrderIdentifier dataToWrite;
	private boolean writeModeEnabled = false;
	private NfcListener listener;
	
	public NfcWrapper(Activity activity) {
		context = activity;
		nfcAdapter = NfcAdapter.getDefaultAdapter(context);
		createSelfReferencingIntent();
	}
	
	private void createSelfReferencingIntent() {
		pendingIntent = PendingIntent.getActivity(
				context,
				0,
				new Intent(context, context.getClass()).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
				0);
	}
	
	public boolean isWriteModeEnabled() {
		return writeModeEnabled;
	}
	
	public void enableReadMode() {
		IntentFilter tagDiscovered = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			tagDiscovered.addDataType(OrderIdentifier.MIME_TYPE);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "", e);
		}
		IntentFilter[] filters = new IntentFilter[] { tagDiscovered };
		nfcAdapter.enableForegroundDispatch(
				context,
				pendingIntent,
				filters,
				null);
	}
	
	public void disableReadMode() {
		nfcAdapter.disableForegroundDispatch(context);
	}
	
	public void prepareWrite(OrderIdentifier order) {
		dataToWrite = order;
		disableReadMode();
		enableWriteMode();
		onWriteModeEnabled();
	}
	
	private void onWriteModeEnabled() {
		if (listener != null) {
			listener.onWriteModeEnabled();
		}
	}
	
	public void cancelWrite() {
		disableWriteMode();
		enableReadMode();
	}
	
	private void enableWriteMode() {
		IntentFilter tagDiscovered = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter[] filters = new IntentFilter[] { tagDiscovered };
		nfcAdapter.enableForegroundDispatch(
				context,
				pendingIntent,
				filters,
				null);
		writeModeEnabled = true;
	}
	
	private void disableWriteMode() {
		nfcAdapter.disableForegroundDispatch(context);
		writeModeEnabled = false;
	}
	
	public boolean handleIntent(Intent intent) {
		boolean handled = false;
		if (isWriteModeEnabled() && isGenericTagDiscovered(intent)) {
			writeDataToTag(intent);
			handled = true;
		} else if (isNdefTagDiscovered(intent)) {
			receiveDataFromIntent(intent);
			handled = true;
		} else {
			Log.i(this.getClass().getName(), "Discovered unknown intent.");
		}
		return handled;
	}

	private boolean isGenericTagDiscovered(Intent intent) {
		return NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction());
	}
	
	private boolean isNdefTagDiscovered(Intent intent) {
		return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
	}
	
	private void writeDataToTag(Intent intent) {
		Tag nfcTag = getTagFromIntent(intent);
		NfcDataSource dataSource = new NfcDataSource(nfcTag);
		NdefMessage message = payloadMapper.mapToMessage(dataToWrite);
		try {
			dataSource.write(message);
			onWriteSuccessful();
		} catch (NfcDataSourceException e) {
			onWriteFailed();
		}
	}
	
	private void onWriteSuccessful() {
		if (listener != null) {
			listener.onWriteSuccessful();
		}
	}
	
	private void onWriteFailed() {
		if (listener != null) {
			listener.onWriteFailed();
		}
	}
	
	private void receiveDataFromIntent(Intent intent) {
		Tag nfcTag = getTagFromIntent(intent);
		NfcDataSource dataSource = new NfcDataSource(nfcTag);
		NdefMessage message = dataSource.read();
		if (message != null) {
			OrderIdentifier order = payloadMapper.mapToPayload(message);
			onNewData(order);
		}
	}
	
	private void onNewData(OrderIdentifier data) {
		if (listener != null) {
			listener.onNewData(data);
		}
	}
	
	private Tag getTagFromIntent(Intent intent) {
		return intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	}
	
	public void addNfcListener(NfcListener newListener) {
		listener = newListener;
	}
}
