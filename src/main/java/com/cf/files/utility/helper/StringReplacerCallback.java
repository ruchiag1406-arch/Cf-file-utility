package com.cf.files.utility.helper;


import java.util.regex.Matcher;

/**
 * Interface for the Recursive Callback.
 */
public interface StringReplacerCallback {
	public String replace(Matcher match) throws Exception;
}