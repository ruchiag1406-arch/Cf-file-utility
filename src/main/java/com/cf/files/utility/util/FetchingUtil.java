package com.cf.files.utility.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.cf.files.utility.model.AttachmentRequestPO;

/**
 * @author raghav.simlote
 *
 */
public class FetchingUtil {
	
//	public static final String INSTANCE_URL = "https://horsepowr--devlatest--c.visualforce.com";
//	public static final String SESSION_ID = "00D1k000000ET2O!AREAQDNOntaPQy20YtOLB1ffoiNpPau9eA3IM4N_CJLw6d49az_ePsAPrijqD0ZrXlVC5rtM2sWM9b_Fdu_0vd_UII4UQj_l";
	
	private static final FetchingUtil INSTANCE = new FetchingUtil();
	
	private FetchingUtil() {
		
	}
	
	public static FetchingUtil getInstance() {
		return INSTANCE;
	}
	
	public InputStream fetchFileFromUrl(String fileUrl){
        try{
        	fileUrl = fileUrl.replace(" ", "%20");
        	fileUrl = fileUrl.replace("+", "%2B");
        	System.out.println("Amazon Url is " + fileUrl);
            URL url = new URL( fileUrl );
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            System.out.println("Response Code " + con.getResponseCode());
            System.out.println("Response Message " + con.getResponseMessage() );
            /*for (String key : con.getHeaderFields().keySet() ) {
            	System.out.println("Header " + con.getHeaderField(key) );
            }*/
            /*for (String key : con.getRequestProperties().keySet() ) {
            	System.out.println("Request " + con.getRequestProperty(key) );
            }*/
            
            return con.getInputStream();
        }catch (Exception e){
            System.out.println("Exception in fetching Amazon file: "+fileUrl);
        }
        return null;
    }
	
	public InputStream getPDFAttachmentConnection(AttachmentRequestPO attachmentRequest, String objectId, String objectType)
			throws Exception {
		
		String instanceUrl = attachmentRequest.getOrgInstance();
		String sessionId = attachmentRequest.getSessionId();
		System.out.println("instanceUrl === " + instanceUrl);
		System.out.println("sessionId === " + sessionId);
		System.out.println("objectId ===" + objectId);
		System.out.println("objectType ===" + objectType);
		System.out.println("Merge Org Id is " + attachmentRequest.getOrgId());
		
		if ( objectId!=null ) {
			return SfConnUtil.getFileStreamFromSF(objectId, objectType, attachmentRequest.getOrgId());
		}
		
		/*
		
		String SALESFORCE_PDF_FETCH_URL = null;

		if (instanceUrl != null && objectId != null && sessionId != null) {

			if( "Attachment".equalsIgnoreCase(objectType) ){
				SALESFORCE_PDF_FETCH_URL = instanceUrl + "/services/data/v43.0/sobjects/" + objectType + "/" + objectId
					+ "/body";
			}else if( "ContentVersion".equalsIgnoreCase(objectType) ){
				SALESFORCE_PDF_FETCH_URL = instanceUrl + "/services/data/v43.0/sobjects/" + objectType + "/" + objectId
					+ "/VersionData";
			}

			System.out.println("SALESFORCE_PDF_FETCH_URL === " + SALESFORCE_PDF_FETCH_URL);
			URL url = new URL(SALESFORCE_PDF_FETCH_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", "Bearer " + sessionId);
			System.out.println("Response Code " + con.getResponseCode());
            System.out.println("Response Message " + con.getResponseMessage() );
            /*for (String key : con.getHeaderFields().keySet() ) {
            	System.out.println("Header " + con.getHeaderField(key) );
            }*/
            /*for (String key : con.getRequestProperties().keySet() ) {
            	System.out.println("Request " + con.getRequestProperty(key) );
            }
			return con.getInputStream();
		}
	*/
		return null;
	}
	
	public static InputStream getPDFFile(String s3url)
			throws Exception {
		
		/*  First decode before encoding again */
		String decodedURL = EncodingUtil.getDecodedUrl(s3url);
		
		/*  Encode File */
		String correctEncodedURL=EncodingUtil.getEncodedUrl(decodedURL);
		
		URL url = new URL(correctEncodedURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/pdf;charset=utf-8");
		System.out.println("Response Code " + con.getResponseCode());
//		System.out.println("Response Message " + con.getResponseMessage());
		return con.getInputStream();

	}
	
	public static int getFileResponseCode(String s3url)
			throws Exception {
		
		/*  First decode before encoding again */
		String decodedURL = EncodingUtil.getDecodedUrl(s3url);
		
		/*  Encode File */
		String correctEncodedURL=EncodingUtil.getEncodedUrl(decodedURL);
		
		URL url = new URL(correctEncodedURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		System.out.println("Response Code " + con.getResponseCode());
//		System.out.println("Response Message " + con.getResponseMessage());
		return con.getResponseCode();

	}
	
	public static InputStream getDirectPDFFile(String s3url)
			throws Exception {
		
		URL url = new URL(s3url);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/pdf;charset=utf-8");
		System.out.println("Response Code " + con.getResponseCode());
		return con.getInputStream();

	}


}
