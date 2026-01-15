package com.cf.files.utility.helper;

import javax.mail.Part;

/**
 * Interface for the Recursive Callback.
 */
public interface WalkMimeCallback {
	public void walkMimeCallback(Part p, int level) throws Exception;
}