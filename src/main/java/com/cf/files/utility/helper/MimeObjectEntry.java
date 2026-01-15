package com.cf.files.utility.helper;

import javax.mail.internet.ContentType;
/**
 * Wrapper class that is used to bundle an object with it's contentType.
 *
 * @param <T> generic type of the entry
 */
public class MimeObjectEntry<T> {
	private T entry;
	private ContentType contentType;

	public MimeObjectEntry(T entry, ContentType contentType) {
		this.entry = entry;
		this.contentType = contentType;
	}

	public T getEntry() {
		return entry;
	}

	public void setEntry(T entry) {
		this.entry = entry;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
}
