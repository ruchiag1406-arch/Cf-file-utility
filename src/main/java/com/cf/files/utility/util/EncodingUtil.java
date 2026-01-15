package com.cf.files.utility.util;

import java.net.IDN;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

public class EncodingUtil {
	
	public static String getEncodedUrl(String str) throws Exception {
		URL url = new URL(str);
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		String estr = uri.toASCIIString();
		System.out.println("Encoded Url is " + estr);		
		return estr; 
	}
	
	public static String getDecodedUrl(String str) throws Exception {
		String dstr = URLDecoder.decode(str, "UTF-8");
		System.out.println("Decoded Url is " + dstr);
		return dstr;
	}
	
	public static String getDecodedStr(String str) throws Exception {
		String dstr = URLDecoder.decode(str, "UTF-8");
		System.out.println("Decoded Str is " + dstr);
		return dstr;
	}
	
	

}
