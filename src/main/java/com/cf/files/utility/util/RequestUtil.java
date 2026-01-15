package com.cf.files.utility.util;

import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.cf.files.utility.exception.InvalidParametersException;
import com.cf.files.utility.model.AttachmentRequestPO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author raghav.simlote
 *
 */
public class RequestUtil {
	
	private static final RequestUtil INSTANCE = new RequestUtil();
	
	private RequestUtil() {
		
	}
	
	public static RequestUtil getInstance() {
		return INSTANCE;
	}
	
	public AttachmentRequestPO prepareAttachmentRequest(HttpServletRequest request) throws InvalidParametersException {
		
		AttachmentRequestPO attachmentRequest = null;
		String requestData = null;
		try {
			
			requestData = request.getReader().lines().collect(Collectors.joining());
			if ( requestData!=null && requestData.length()>10 ) {
				requestData = requestData.replaceAll("jsonData=", "");
				requestData = EncodingUtil.getDecodedStr(requestData);
				System.out.println("RD " + requestData);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, String[]> reqMap = request.getParameterMap();
		System.out.println("Input is " + reqMap);
		for (String name: reqMap.keySet() ) {
		    String key = name.toString();
		    String value = reqMap.get(name).toString();
		    System.out.println(key + " " + value);
		}
		
		if ( requestData!=null && requestData.length()>10 ) { 
			
			try {
				
				GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();
				attachmentRequest = gson.fromJson(requestData, AttachmentRequestPO.class);
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidParametersException(e.getMessage(), e);
			}
			
		} else {
			
			try {
				
				if (reqMap.containsKey("jsonData")  ) {

					String inputJson = EncodingUtil.getDecodedStr( reqMap.get("jsonData")[0] );
					System.out.println("Input Json is " + inputJson);
					GsonBuilder gsonBuilder = new GsonBuilder();
					Gson gson = gsonBuilder.create();
					attachmentRequest = gson.fromJson(inputJson, AttachmentRequestPO.class);
						
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidParametersException(e.getMessage(), e);
			}
		}
		
		
		
		
		System.out.println("Attachment Ids " + attachmentRequest.getAttachmentIds());
		System.out.println("Content document ids " + attachmentRequest.getContentDocumentIds());
		System.out.println("Session Id " + attachmentRequest.getSessionId());
		System.out.println("Unique Id " + attachmentRequest.getUniqueId());
		System.out.println("Org instance " + attachmentRequest.getOrgInstance());
		System.out.println("Parent id " + attachmentRequest.getParentId());
		System.out.println("File url" + attachmentRequest.getFileUrls());
		System.out.println("Files" + attachmentRequest.getFiles());
		System.out.println("Org Bucket Name" + attachmentRequest.getOrgBucketName());
		System.out.println("Sub Bucket Name" + attachmentRequest.getBucketName());
		System.out.println("S Object Name" + attachmentRequest.getsObjectName());
		System.out.println("BaseUrl " + attachmentRequest.getBaseUrl());
		
		//byte[] decodedBytes = Base64.getUrlDecoder().decode(attachmentRequest.getOrgId());
		//String orgId = new String(decodedBytes);
		//attachmentRequest.setOrgId(orgId);
		System.out.println("orgId " + attachmentRequest.getOrgId());
		
//		Testing and ensuring parameters before processing
		if (attachmentRequest != null
				&& (attachmentRequest.getAttachmentIds() != null
						&& !attachmentRequest.getAttachmentIds().isEmpty())
				|| (attachmentRequest.getContentDocumentIds() != null
						&& !attachmentRequest.getContentDocumentIds().isEmpty())
				|| (attachmentRequest.getFileUrls() != null
						&& !attachmentRequest.getFileUrls().isEmpty())
				|| (attachmentRequest.getFiles() != null
						&& !attachmentRequest.getFiles().isEmpty())

						&& attachmentRequest.getSessionId() != null
						&& attachmentRequest.getOrgInstance() != null
						&& attachmentRequest.getParentId() != null) {
			
		} else {
			throw new InvalidParametersException("Attachment Request Object doesnt contains correct Values", null);
		}
		
		return attachmentRequest;
		
	}
	
}
