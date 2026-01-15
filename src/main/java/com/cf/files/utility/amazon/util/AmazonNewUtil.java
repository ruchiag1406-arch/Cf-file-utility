package com.cf.files.utility.amazon.util;

import java.io.File;

import com.cf.files.utility.amazon.service.AmazonService;
import com.cf.files.utility.util.EncodingUtil;

public class AmazonNewUtil {
	
	 public static boolean uploadS3FileWithUrl( String fileStr, String s3Url, String orgId) {
		 
		 boolean isDone = true;
	    	final File multipartFile = new File(fileStr);
	    	
	        try {
	        	
	        	String tok1 = "https://";
	        	String tok2 = ".s3.amazonaws.com/";
	        	int startBuckIdx = s3Url.indexOf(tok1);
	        	int endBuckIdx = s3Url.indexOf(tok2);
	        	final String buckName = s3Url.substring(startBuckIdx + tok1.length(), endBuckIdx);
	        	System.out.println("S3 Upload Bucket Name is " + buckName);
	        	
	        	final String fileName = s3Url.substring(endBuckIdx+tok2.length());
	        	System.out.println("S3 Upload File Name is " + fileName);
	        	
	        	
	        	final String fName;
	        	
	        	/* Fix added 29-11-21 */
	        	int xIdx = fileName.indexOf("?X-Amz");
				if ( xIdx > 0  ) {
					String fileTest = fileName.substring(0, xIdx);
					fName = EncodingUtil.getDecodedStr(fileTest);
					System.out.println("Signed Full url is " + fName);
					
				} else {
					fName = fileName;
					System.out.println("Signed Full other url is " + fName);
				}
	        	
	            new Thread(new Runnable() {
	    			
	    			public void run() {
	    				// TODO Auto-generated method stub
	    				//uploadFileTos3Url(buckName, fName, multipartFile);
	    				new AmazonService().uploadFileTos3Url(orgId, fName, multipartFile);
	    				try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							System.out.println("System sleep not done");
						}
//	    				multipartFile.delete();
	    			}
	    		}).start(); 
	            
	            
	        } catch (Exception e) {
	        	isDone = false;
	           e.printStackTrace();
	        }
	       
	        return isDone;
		 
	 }

}
