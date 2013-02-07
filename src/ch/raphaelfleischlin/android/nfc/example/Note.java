package ch.raphaelfleischlin.android.nfc.example;

import ch.raphaelfleischlin.android.nfc.Payload;

public class Note implements Payload {
	
	private static final long serialVersionUID = 2739202728159227731L;

	private String title;
	
	private String content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
