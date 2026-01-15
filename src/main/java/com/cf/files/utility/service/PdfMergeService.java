package com.cf.files.utility.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cf.files.utility.exception.InvalidParametersException;
import com.cf.files.utility.model.AttachmentRequestPO;
import com.cf.files.utility.model.FilePO;
import com.cf.files.utility.model.MergeFilePO;
import com.cf.files.utility.util.FetchingUtil;
import com.cf.files.utility.util.MergingUtil;
import com.cf.files.utility.util.RequestUtil;
import com.itextpdf.text.DocumentException;

/**
 * @author raghav.simlote
 *
 */
public class PdfMergeService {

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/plain");
		
		AttachmentRequestPO attachmentRequest = null;
		boolean ensureParameters = false;
		try {
			attachmentRequest = RequestUtil.getInstance().prepareAttachmentRequest(request);
			ensureParameters = true;
		} catch ( InvalidParametersException ipe ) {
			
//			response.sendError(400, "Invalid JSON or Attachment Request");
			
		}

		try {

			// parse parameters received
//			parseParameterValues(request);

			if ( ensureParameters ) {
				
				FetchingUtil fetchingUtil = FetchingUtil.getInstance();
				
				ArrayList<MergeFilePO> lstStream = new ArrayList<MergeFilePO>();
//				ArrayList<InputStream> lstStream = new ArrayList<InputStream>();
				System.out.println("File request wrapper received>>"+attachmentRequest.getFiles());
				if(attachmentRequest.getFiles() != null && !attachmentRequest.getFiles().isEmpty()){
					
					for(FilePO file : attachmentRequest.getFiles()){
						System.out.println("File before condition>>"+file.getRecordId() + ">>"+file.getFileType());
						if( file.getFileUrl() != null && "Amazon".equalsIgnoreCase( file.getFileType() ) ){
							InputStream fileStream = fetchingUtil.fetchFileFromUrl(file.getFileUrl() );
							if(fileStream != null){
								MergeFilePO mergeFilePO = new MergeFilePO();
								mergeFilePO.setFileExtensionType(file.getFileExtensionType());
								mergeFilePO.setFileStream(fileStream);
								lstStream.add(mergeFilePO);
//								lstStream.add(fileStream);
							}
						}else if( file.getRecordId() != null && "Attachment".equalsIgnoreCase( file.getFileType() ) ){
							InputStream fileStream = fetchingUtil.getPDFAttachmentConnection(attachmentRequest, file.getRecordId(), file.getFileType());
							if(fileStream != null){
								MergeFilePO mergeFilePO = new MergeFilePO();
								mergeFilePO.setFileExtensionType(file.getFileExtensionType());
								mergeFilePO.setFileStream(fileStream);
								lstStream.add(mergeFilePO);
//								lstStream.add(fileStream);
							}
						}else if(file.getRecordId() != null && "ContentVersion".equalsIgnoreCase( file.getFileType() ) ){
							System.out.println("In content >>"+file.getRecordId() + ">>"+file.getFileType());
							InputStream fileStream = fetchingUtil.getPDFAttachmentConnection(attachmentRequest, file.getRecordId(), file.getFileType());
							if(fileStream != null){
								MergeFilePO mergeFilePO = new MergeFilePO();
								mergeFilePO.setFileExtensionType(file.getFileExtensionType());
								mergeFilePO.setFileStream(fileStream);
								lstStream.add(mergeFilePO);
//								lstStream.add(fileStream);
							}
						}else{
							System.out.println("File not stream found>>"+file.getRecordId() + ">>"+file.getFileType()+">>>");
						}
					}
				}


				if (attachmentRequest.getAttachmentIds() != null) {
					for (String attachmentId : attachmentRequest.getAttachmentIds()) {
						InputStream fileStream = fetchingUtil.getPDFAttachmentConnection(attachmentRequest, attachmentId, "Attachment");
						if(fileStream != null){
							MergeFilePO mergeFilePO = new MergeFilePO();
//							mergeFilePO.setFileExtensionType(file.getFileExtensionType());
							mergeFilePO.setFileStream(fileStream);
							lstStream.add(mergeFilePO);
//							lstStream.add(fileStream);
						}
					}
				}

				if (attachmentRequest.getContentDocumentIds() != null) {
					for (String contentId : attachmentRequest.getContentDocumentIds()) {
						InputStream fileStream = fetchingUtil.getPDFAttachmentConnection(attachmentRequest, contentId, "ContentVersion");
						if(fileStream != null){
							MergeFilePO mergeFilePO = new MergeFilePO();
//							mergeFilePO.setFileExtensionType(file.getFileExtensionType());
							mergeFilePO.setFileStream(fileStream);
							lstStream.add(mergeFilePO);
//							lstStream.add(fileStream);
						}

					}
				}
				if(attachmentRequest.getFileUrls() != null){
                    for (String contentId : attachmentRequest.getFileUrls()) {
                        InputStream fileStream = fetchingUtil.fetchFileFromUrl(contentId);
                        if(fileStream != null){
							MergeFilePO mergeFilePO = new MergeFilePO();
//							mergeFilePO.setFileExtensionType(file.getFileExtensionType());
							mergeFilePO.setFileStream(fileStream);
							lstStream.add(mergeFilePO);
//							lstStream.add(fileStream);
						}
                    }
                }
				if (!lstStream.isEmpty()) {
					
					MergingUtil.getInstance().doMerge(lstStream, response, attachmentRequest);
				} else {
					System.out.println("List stream is empty>>"+lstStream.size());
					response.sendError(400, "Few parameters to merge");
				}

				/*
				 * HttpURLConnection firstPDFConnection =
				 * getPDFAttachmentConnection(
				 * allParamsMap.get("firstAttachmentId")); HttpURLConnection
				 * secondPDFConnection = getPDFAttachmentConnection(
				 * allParamsMap.get("secondAttachmentId"));
				 * System.out.println("instanceUrl === " +
				 * firstPDFConnection.getResponseCode() + "  ==== " +
				 * secondPDFConnection.getResponseCode()); if
				 * (firstPDFConnection != null && secondPDFConnection != null &&
				 * firstPDFConnection.getResponseCode() == 200 &&
				 * secondPDFConnection.getResponseCode() == 200) { // merge PDFs
				 * mergePDFs(firstPDFConnection,secondPDFConnection , response);
				 *
				 * } else { String errorMessageInFetching = firstPDFConnection
				 * == null || secondPDFConnection == null ?
				 * "Unable to read the attachments. Problem in establishing connection."
				 * : (firstPDFConnection.getResponseCode() != 200 ?
				 * firstPDFConnection.getResponseMessage() :
				 * secondPDFConnection.getResponseMessage());
				 *
				 * Integer errorCode = firstPDFConnection == null ||
				 * secondPDFConnection == null ? 503 :
				 * (firstPDFConnection.getResponseCode() != 200 ?
				 * firstPDFConnection.getResponseCode() :
				 * secondPDFConnection.getResponseCode());
				 * response.sendError(errorCode, errorMessageInFetching); }
				 */
			} else {
				System.out.println("ensureParameters>>>"+ensureParameters);
				response.sendError(400, "Invalid parameters");
			}
		} catch (UnsupportedEncodingException encodingEx) {
			// response.setStatus(422);
			response.sendError(422, encodingEx.getMessage());
		} catch (IOException ioEx) {
			System.out.println("Exception:"+ioEx.getMessage());
			// response.setStatus(422);
			response.sendError(503, ioEx.getMessage() + "--- " + ioEx.getStackTrace());
		} catch (DocumentException de) {
			response.sendError(503, de.getMessage() + "--- " + de.getStackTrace());
		} catch (Exception e) {
			response.sendError(400, e.getMessage() + "--- " + e.getStackTrace());
		}
		
	}
	
	

}
