package ch.raphaelfleischlin.android.nfc.example;

import ch.raphaelfleischlin.android.nfc.NfcAwareActivity;
import ch.raphaelfleischlin.android.nfc.R;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends NfcAwareActivity<Note> implements OnClickListener {
	
	public MainActivity() {
		super(Note.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button saveButton = (Button)findViewById(R.id.save);
		saveButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Note note = new Note();
		note.setTitle(((EditText)findViewById(R.id.title)).getText().toString());
		note.setContent(((EditText)findViewById(R.id.content)).getText().toString());
		prepareWrite(note);
	}
	
	@Override
	public void onNewData(Note data) {
		AlertDialog alert = new AlertDialog.Builder(this)
			.setTitle(data.getTitle())
			.setMessage(data.getContent())
			.create();
		alert.show();
	}

}
