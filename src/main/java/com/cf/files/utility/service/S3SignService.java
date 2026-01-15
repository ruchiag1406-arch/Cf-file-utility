package com.cf.files.utility.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import com.cf.files.utility.amazon.AmazonUtil;
import com.cf.files.utility.model.PdfSignRequest;
import com.cf.files.utility.util.Constants;
import com.cf.files.utility.util.EncodingUtil;
import com.cf.files.utility.util.FetchingUtil;
import com.cf.files.utility.util.SfConnUtil;

public class S3SignService {
	
	public String getFileUrl(String recordId, String org) {
		
		String fileUrl = null;
		try {
			
		} catch (Exception e) {
			
		}
		return fileUrl;
		
	}
	
	public boolean checkExpiry(PdfSignRequest pdfSignRequest) throws Exception {
		
		String s3url = pdfSignRequest.getAmazonS3Url();
		//String exDate = pdfSignRequest.getExpDate();
		
		int exIdx = s3url.indexOf("Expires=");
		int sigIdx = s3url.indexOf("&Signature=");
		int accIdx = s3url.indexOf("AWSAccessKeyId");
		int hpeIdx = s3url.indexOf("horsepowerelectric");
		
		if ( exIdx > 0 ) {
			
			try {
				
				String exT = s3url.substring(exIdx+8, sigIdx) + "000";
				System.out.println("Url Exp Time " + exT);
				
				if ( accIdx <= 0 ) {
					s3url = s3url + "&AWSAccessKeyId=" + Constants.ACCESS_KEY;
					pdfSignRequest.setAmazonS3Url(s3url);
					System.out.println("Fixed Full url is " + s3url);
				}
				
				
				
				long exL = Long.parseLong(exT);
				
				long currTimeMillis = Instant.now().toEpochMilli();
	            System.out.println("Current Time " + currTimeMillis);
				
				//long diff = expTimeMillis-exL;
				//long expiration = 1000 * 60 * 60 * 24 * 7;
				
				if ( currTimeMillis > exL) {
					return true;
				} else {
					return false;
				}
				
				
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			
		} else {
			/* url doesnt have expire parameter but it has hpe bucket */
			if (hpeIdx>0 ) {
				return true;
			}
			//return false;
		}
		
		/* s3url = s3url.replace(" ", "%20");
		s3url = s3url.replace("+", "%2B"); */
    	
        
		int code = FetchingUtil.getFileResponseCode(s3url);
		System.out.println("Amazon Url is " + s3url);
		if ( code == 200 ) {
			return false;
		} else {
			return true;
		}

	}
	
	public boolean checkExpiryNew(PdfSignRequest pdfSignRequest) throws Exception {
		
		String s3url = pdfSignRequest.getAmazonS3Url();
		System.out.println("URL parsing " + s3url);
		//String exDate = pdfSignRequest.getExpDate();
		
//		&X-Amz-Date=20211110T040427Z
		String exParam = "&X-Amz-Date=";
		int exIdx = s3url.indexOf(exParam);
		System.out.println("ExParam Index " + exIdx);
		int hpeIdx = s3url.indexOf("horsepowerelectric");
		
		if ( exIdx > 0 ) {
			
			try {
				
				int endIdx = s3url.indexOf("T", exIdx+exParam.length());
				System.out.println("End Index " + endIdx);
				String exStr = s3url.substring(exIdx+exParam.length(), endIdx);
				System.out.println("Url Exp String Date " + exStr);
				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
				Date date = format1.parse(exStr);
				long exL = date.getTime();
				System.out.println("Expired Time 1 " + exL);
				exL += 1000 * 60 * 60 * 24 * 7;
				System.out.println("Expired Time 2 " + exL);
				
				long currTimeMillis = Instant.now().toEpochMilli();
	            System.out.println("Current Time " + currTimeMillis);
	            
	            if ( currTimeMillis > exL) {
					return true;
				} else {
					return false;
				}
			
				
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			
		} else {
			/* url doesnt have expire parameter but it has hpe bucket */
			if (hpeIdx>0 ) {
				return true;
			}
			//return false;
		}
		
		/* s3url = s3url.replace(" ", "%20");
		s3url = s3url.replace("+", "%2B"); */
    	
        
		int code = FetchingUtil.getFileResponseCode(s3url);
		System.out.println("Amazon Url is " + s3url);
		if ( code == 200 ) {
			return false;
		} else {
			return true;
		}

	}
	
	public String generateFileUrl(String fileUrl1, String recordId, String org) {
		
		String tok1 = "https://";
    	String tok2 = ".s3.amazonaws.com/";
    	int startBuckIdx = fileUrl1.indexOf(tok1);
    	int endBuckIdx = fileUrl1.indexOf(tok2);
    	final String buckName = fileUrl1.substring(startBuckIdx + tok1.length(), endBuckIdx);
    	System.out.println("S3 Upload Bucket Name is " + buckName);
    	
    	int qIdx = fileUrl1.lastIndexOf('?');
    	
    	String fileN = null;
    	if ( qIdx > 0 ) {
    		fileN = fileUrl1.substring(endBuckIdx+tok2.length(), qIdx);
    	} else {
    		fileN = fileUrl1.substring(endBuckIdx+tok2.length());
    	}
    	
    	/* fileN = fileN.replace("%20"," ");
    	fileN = fileN.replace("%2B","+"); */
    	
    	/* Decode File Name */
    	try {
			fileN = EncodingUtil.getDecodedStr(fileN);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	final String fileName = new String(fileN);
    	System.out.println("S3 Upload File Name is " + fileName);
    	
    	
		String fileUrl = AmazonUtil.generateFileUrl(org, fileName);
		
		/* save record in SF */
    	SfConnUtil.saveFileUrlInSf(recordId, org, fileUrl);
    	
    	return fileUrl;
		
	}
	
	public String genUrlForPdfSign(String fileUrl1, String recordId, String org) {
		
		String tok1 = "https://";
    	String tok2 = ".s3.amazonaws.com/";
    	int startBuckIdx = fileUrl1.indexOf(tok1);
    	int endBuckIdx = fileUrl1.indexOf(tok2);
    	final String buckName = fileUrl1.substring(startBuckIdx + tok1.length(), endBuckIdx);
    	System.out.println("S3 Upload Bucket Name is " + buckName);
    	
    	int qIdx = fileUrl1.lastIndexOf('?');
    	
    	String fileN = null;
    	if ( qIdx > 0 ) {
    		fileN = fileUrl1.substring(endBuckIdx+tok2.length(), qIdx);
    	} else {
    		fileN = fileUrl1.substring(endBuckIdx+tok2.length());
    	}
    	
    	/* fileN = fileN.replace("%20"," ");
    	fileN = fileN.replace("%2B","+"); */
    	
    	/* Decode File Name */
    	try {
			fileN = EncodingUtil.getDecodedStr(fileN);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	final String fileName = new String(fileN);
    	System.out.println("S3 Upload File Name is " + fileName);
    	
    	
		String fileUrl = AmazonUtil.generateFileUrl(org, fileName);
		
		/* save record in SF */
    	//SfConnUtil.saveFileUrlInSf(recordId, org, fileUrl);
    	
    	return fileUrl;
		
	}

}
