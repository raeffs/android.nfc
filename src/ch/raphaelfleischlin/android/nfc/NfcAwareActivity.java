package ch.raphaelfleischlin.android.nfc;

import ch.hslu.pawi.h12.pizzatracker.android.OrderIdentifier;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
* An activity that is able to interact with NFC tags.
* Provides methods to write to tags and gets notified if data is received from a tag.
*/
public abstract class NfcAwareActivity extends Activity implements NfcListener, OnCancelListener {
	
	private NfcWrapper nfcWrapper;
	private AlertDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nfcWrapper = new NfcWrapper(this);
		nfcWrapper.addNfcListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nfcWrapper.enableReadMode();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		nfcWrapper.disableReadMode();
	}
	
	protected void prepareWrite(OrderIdentifier order) {
		nfcWrapper.prepareWrite(order);
	}
	
	public void onWriteModeEnabled() {
		showWriteNotification();
	}
	
	private void showWriteNotification() {
		dialog = new AlertDialog.Builder(this)
				.setTitle("Touch NFC tag to write data")
				.setOnCancelListener(this)
				.create();
		dialog.show();
	}
	
	public void onCancel(DialogInterface dialog) {
		nfcWrapper.cancelWrite();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (!nfcWrapper.handleIntent(intent)) {
			super.onNewIntent(intent);
		}
	}
	
	public void onWriteSuccessful() {
		showToast("Successfully wrote data to NFC tag!");
		dialog.cancel();
	}
	
	public void onWriteFailed() {
		showToast("Failed to write data to NFC tag!");
		dialog.cancel();
	}
	
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
}
