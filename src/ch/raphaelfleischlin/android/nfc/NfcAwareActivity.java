package ch.raphaelfleischlin.android.nfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 
 * @author Raphael Fleischlin <raphael.fleischlin at gmail.com>
 */
public abstract class NfcAwareActivity<T extends Payload> extends Activity implements NfcListener<T>, OnCancelListener {
	
	private Class<T> clazz;
	private NfcWrapper<T> nfcWrapper;
	private AlertDialog dialog;
	
	public NfcAwareActivity(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nfcWrapper = new NfcWrapper<T>(this, clazz);
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
	
	protected void prepareWrite(T data) {
		nfcWrapper.prepareWrite(data);
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
