package com.cf.files.utility.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple class to replace substrings with a callback function.
 */
public class StringReplacer {
	/**
	 * Replaces occurences of the regex using a callback function.
	 * @param input String to modify
	 * @param regex regex
	 * @param callback the occurences of the regex are replaced with the result of the callback function
	 * @return modified string
	 * @throws Exception
	 */
	public static String replace(String input, Pattern regex, StringReplacerCallback callback) throws Exception {
		StringBuffer resultString = new StringBuffer();
		Matcher regexMatcher = regex.matcher(input);
		while (regexMatcher.find()) {
			regexMatcher.appendReplacement(resultString, Matcher.quoteReplacement(callback.replace(regexMatcher)));
		}
		regexMatcher.appendTail(resultString);

		return resultString.toString();
	}
}