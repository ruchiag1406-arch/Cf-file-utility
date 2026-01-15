package com.cf.files.utility.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.poifs.filesystem.FileMagic;

import com.cf.files.utility.amazon.AmazonObj;
import com.cf.files.utility.amazon.AmazonUtil;
import com.cf.files.utility.model.AttachmentRequestPO;
import com.cf.files.utility.model.MergeFilePO;
import com.cf.files.utility.pdf.converter.DefaultConverter;
import com.cf.files.utility.pdf.converter.EmlConvertor;
import com.cf.files.utility.pdf.converter.ExcelConverter;
import com.cf.files.utility.pdf.converter.ImageConverter;
import com.cf.files.utility.pdf.converter.MsgConvertor;
import com.cf.files.utility.pdf.converter.WordConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

/**
 * @author raghav.simlote
 *
 */
public class MergingUtil {
	
	private static final MergingUtil INSTANCE = new MergingUtil();
	
	private MergingUtil() {
		
	}
	
	public static MergingUtil getInstance() {
		return INSTANCE;
	}
	
	public void doMerge(ArrayList<MergeFilePO> list, HttpServletResponse response,
			AttachmentRequestPO attachmentRequest) throws DocumentException, IOException {
		//System.gc();
		System.out.println("Inside Merge 1");

		File tempFile = File.createTempFile(attachmentRequest.getAttachmentName(), ".pdf");
		OutputStream fos = new FileOutputStream(tempFile);

//		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Create a PdfCopyFields object
		Document copyDocument = new Document();
		PdfCopy pdfCopy = new PdfCopy(copyDocument, fos);
//		pdfCopy.setMergeFields();
		copyDocument.open();
//		Deprecated
//		PdfCopyFields copy = new PdfCopyFields(fos);

		// Bad Password Exception if removed
		PdfReader.unethicalreading = true;
		
		int fileCount=0;
//		int k=0;
		
		for (MergeFilePO mergeFilePO : list) {
			
			
			InputStream in = mergeFilePO.getFileStream();
			String mimeType = URLConnection.guessContentTypeFromStream(in);
			System.out.println("MIME Type 1 is "  + mimeType);
			
			
			String targFileName = "targFile"+ attachmentRequest.getUniqueId() + (++fileCount);
			String tempTargFileName = "tempTargFile"+ attachmentRequest.getUniqueId() + (fileCount);
			
			File targetFile = new File(targFileName);
			FileUtils.copyInputStreamToFile(in, targetFile);
			if ( mimeType == null ) {
				mimeType = Files.probeContentType(Paths.get(targetFile.toURI()));
				System.out.println("MIME Type 2 is "  + mimeType);
			}
			
			
			
			if ( mimeType == null  ) {
				
				BufferedInputStream bisCheck = new BufferedInputStream(new FileInputStream(targetFile));
				FileMagic fileValue = FileMagic.valueOf(bisCheck);
				System.out.println("File Value is " + fileValue);
				System.out.println("Doc is " + FileMagic.OLE2);
				System.out.println("Doc is " + FileMagic.OOXML);
				
				if ( fileValue!=null ) {
					mimeType = fileValue.toString();
				}
				
				
				System.out.println("MIME Type 3 is "  + mimeType);
			}
			
		
			if ( mimeType!=null ) {
				mimeType = mimeType.toLowerCase();
			} 
			
			System.out.println("MIME Type is "  + mimeType);
			
			//System.gc();
			
			if ( mimeType.contains( "pdf".toLowerCase() ) ) {
				
				System.out.println(fileCount + " PDF Size is " + targetFile.length());
				
//				FileInputStream fosTarg = new FileInputStream(targetFile);
				PdfReader reader = new PdfReader(targFileName);
//				copy.addDocument(reader);
				pdfCopy.addDocument(reader);
//				fosTarg.close();
//				in.close();
				reader.close();
				
			}
			else if ( mimeType.contains( "image".toLowerCase() ) ) {
				
				System.out.println("Not in Control Image" + mimeType);
				
				File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
				ImageConverter imageConverter = new ImageConverter(targetFile, tempFileK);
				imageConverter.convertToPdf();
				
				FileInputStream fosIn = new FileInputStream(tempFileK);
			    PdfReader reader = new PdfReader(fosIn);
//				copy.addDocument(reader);
				pdfCopy.addDocument(reader);
				fosIn.close();
				reader.close();
				
			}
			else if ( mimeType.contains( "eml".toLowerCase() ) || mimeType.contains( "message".toLowerCase() ) ) {
				
				System.out.println("Not in Control EML TYPE" + mimeType);
				
				File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
				EmlConvertor emlConvertor = new EmlConvertor(targetFile, tempFileK);
				emlConvertor.convertToPdf();
				
				FileInputStream fosIn = new FileInputStream(tempFileK);
			    PdfReader reader = new PdfReader(fosIn);
//				copy.addDocument(reader);
				pdfCopy.addDocument(reader);
				fosIn.close();
				reader.close();
				
			}  else if ( mimeType.contains( "msg".toLowerCase() ) || mimeType.contains( "x-ole-storage".toLowerCase() ) ) {
				
				System.out.println("Not in Control MSG TYPE" + mimeType);
				
				File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
				MsgConvertor msgConvertor = new MsgConvertor(targetFile, tempFileK);
				msgConvertor.convertToPdf();
				
				FileInputStream fosIn = new FileInputStream(tempFileK);
			    PdfReader reader = new PdfReader(fosIn);
//				copy.addDocument(reader);
				pdfCopy.addDocument(reader);
				fosIn.close();
				reader.close();
				
			}
			else {
				
				System.out.println("Not in Control Doc Else" + mimeType);
				
//				String fileExtension = getFileExtension(targetFile);
				String fileExtension = mergeFilePO.getFileExtensionType()!=null ? mergeFilePO.getFileExtensionType() : getFileExtension(targetFile);;
				if ( fileExtension!=null ) {
					fileExtension=fileExtension.toLowerCase();
				}
				System.out.println("File Extension is " + fileExtension);
				
				if ( fileExtension.endsWith("docx") || fileExtension.endsWith("doc") || fileExtension.endsWith("word_x") || fileExtension.endsWith("word") ) {
//				if ( mimeType.contains( "application/zip".toLowerCase() ) ) {
					
					File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
					WordConverter wordConverter = new WordConverter(targetFile, tempFileK);
					wordConverter.convertToPdf();
					
					FileInputStream fosIn = new FileInputStream(tempFileK);
				    PdfReader reader = new PdfReader(fosIn);
//					copy.addDocument(reader);
					pdfCopy.addDocument(reader);
					fosIn.close();
					reader.close();
			        
				} else if ( fileExtension.endsWith("xlsx") || fileExtension.endsWith("xls") || fileExtension.endsWith("excel_x") || fileExtension.endsWith("excel") ) {
//				} else if ( mimeType.contains( "application/zip".toLowerCase() ) ) {
					
					
					File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
					ExcelConverter excelConverter = new ExcelConverter(targetFile, tempFileK, fileExtension);
					excelConverter.convertToPdf();
					
					FileInputStream fosIn = new FileInputStream(tempFileK);
				    PdfReader reader = new PdfReader(fosIn);
//					copy.addDocument(reader);
					pdfCopy.addDocument(reader);
					fosIn.close();
					reader.close();
					
				}  else {
					
					File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
					DefaultConverter defaultConverter = new DefaultConverter(targetFile, tempFileK);
					defaultConverter.convertToPdf();
				    
				    FileInputStream fosIn = new FileInputStream(tempFileK);
				    PdfReader reader = new PdfReader(fosIn);
//					copy.addDocument(reader);
					pdfCopy.addDocument(reader);
					fosIn.close();
					reader.close();
					
				}
				
			}
			
			
		}
		
		try {
//			copy.close();
//			copyDocument.close();
			pdfCopy.close();
		} catch (Exception e ) {
			e.printStackTrace();
		}
		
//		saveAttachment(attachmentRequest.getParentId(), attachmentRequest.getAttachmentName(), baos, response);
		saveAttachment(attachmentRequest, tempFile, response);
//		baos.flush();
//		baos.close();

	}
	
	public String getFileName(File file) {
		String fileName = file.getName();
		return fileName;
	}

	public String getFileExtension(File file) {
		String fileName = file.getName();
		return fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
	}

	private void saveAttachment(AttachmentRequestPO attachmentRequest, File body,
			HttpServletResponse servletResponse) throws IOException {
//	private void saveAttachment(String parentId, String name, ByteArrayOutputStream body,
//			HttpServletResponse servletResponse) throws IOException {
		
		String parentId = attachmentRequest.getParentId();
		String name = attachmentRequest.getAttachmentName();
		String restURI = attachmentRequest.getOrgInstance() + "/services/data/v43.0/sobjects/attachment/";

		try {

//			Code added to copy file on amazon s3 server
			AmazonObj amazonObj =null;
			try {
//				File file = new File(name + ".pdf");
//				FileUtils.writeByteArrayToFile(file, body.toByteArray());
//				amazonObj = AmazonUtil.uploadFile(body, attachmentRequest.getParentId());
				
				/* changes for presigned url */
//				amazonObj = AmazonUtil.uploadFile(body, attachmentRequest);
				amazonObj = AmazonUtil.uploadFileWithUrl(body, attachmentRequest);
				amazonObj.setContentSize(body.length());
				
			} catch (Exception e) {
				System.out.println("Amazon Thread Exception" + e);
			}

			/* if ( body.length() < (20 * 1000000 ) ) {

				name = (name == null || name.equals("")) ? "MergedDoc" : name;

				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpPost uploadFile = new HttpPost(restURI);
				uploadFile.setHeader("Authorization", "Bearer " + attachmentRequest.getSessionId());
				// uploadFile.addHeader("content-type", "multipart/form-data");

				JSONObject attachment = new JSONObject();
				attachment.put("parentId", parentId);
				attachment.put("name", name + ".pdf"); // .pdf
				attachment.put("ContentType", "application/pdf"); // application/

				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				// builder.setBoundary("19dj0d239d23d");
				builder.addTextBody("entity_attachment", attachment.toString(), ContentType.APPLICATION_JSON);

				name = (name == null || name.equals("")) ? "MergedDoc" : name;

//	          This attaches the file to the POST:
				byte[] array = Files.readAllBytes(body.toPath());
				builder.addBinaryBody("Body", array, ContentType.MULTIPART_FORM_DATA, name + ".pdf");

				HttpEntity multipart = builder.build();

				uploadFile.setEntity(multipart);

				CloseableHttpResponse response = httpClient.execute(uploadFile);
				StringWriter writer = new StringWriter();
				IOUtils.copy(response.getEntity().getContent(), writer, StandardCharsets.UTF_8);

				JSONObject jsonObject = new JSONObject(writer.toString());
				if ( jsonObject !=null ) {
					amazonObj.setAttachmentId(jsonObject.getString("id"));
				}


				System.out.println("Upload code " + response.getStatusLine().getStatusCode() + " Entity is " + amazonObj.getAttachmentId() );

			} */








//			JSONObject respObject = new JSONObject();
//			respObject.put("file_url", fileUrl);

			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();
			String jsonInString = gson.toJson(amazonObj);



//			IOUtils.copy(respObject.toString(), writer, StandardCharsets.UTF_8);
//			String responseStringFromSalesforce = writer.toString();
//			String responseStringFromSalesforce = new String(respObject.toString().getBytes(), StandardCharsets.UTF_8);
			String responseStringFromSalesforce = new String(jsonInString.getBytes(), StandardCharsets.UTF_8);

//			System.out.println(
//					"getStatusCode() === " + response.getStatusLine().getStatusCode() + " === 	getReasonPhrase() "
//							+ response.getStatusLine().getReasonPhrase() + " ==== " + responseStringFromSalesforce);
//
//			servletResponse.setStatus(response.getStatusLine().getStatusCode());
			servletResponse.setStatus(200);
			servletResponse.getWriter().write(responseStringFromSalesforce);
			servletResponse.getWriter().flush();
			servletResponse.getWriter().close();
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("exception === " + e.getMessage());
			servletResponse.sendError(400, e.getMessage());
		}
	}
	
	public void doMerge(List<String> list, String finalUrl) throws DocumentException, IOException {
		
		//System.gc();
		System.out.println("Inside Merge");
		
		FetchingUtil fetchingUtil = FetchingUtil.getInstance();
		ArrayList<MergeFilePO> lstStream = new ArrayList<MergeFilePO>();
		for(String fileUrl : list) {
			InputStream fileStream = fetchingUtil.fetchFileFromUrl(fileUrl);
			MergeFilePO mergeFilePO = new MergeFilePO();
			mergeFilePO.setFileExtensionType(".pdf");
			mergeFilePO.setFileStream(fileStream);
			lstStream.add(mergeFilePO);
		}
		String uniqueString = UUID.randomUUID().toString();
		
		String tempFileN = "tempTargFile"+ uniqueString + "final";
		File tempFile = new File(tempFileN);
		
		//File tempFile = File.createTempFile("attachmentName1", ".pdf");
		OutputStream fos = new FileOutputStream(tempFile);

//		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Create a PdfCopyFields object
		Document copyDocument = new Document();
		PdfCopy pdfCopy = new PdfCopy(copyDocument, fos);
//		pdfCopy.setMergeFields();
		copyDocument.open();
//		Deprecated
//		PdfCopyFields copy = new PdfCopyFields(fos);

		// Bad Password Exception if removed
		PdfReader.unethicalreading = true;
		
		int fileCount=0;
//		int k=0;
		
		for (MergeFilePO mergeFilePO : lstStream) {
			
			
			InputStream in = mergeFilePO.getFileStream();
			String mimeType = URLConnection.guessContentTypeFromStream(in);
			System.out.println("MIME Type 1 is "  + mimeType);
			
			
			String targFileName = "targFile"+ uniqueString + (++fileCount);
			String tempTargFileName = "tempTargFile"+ uniqueString + (fileCount);
			
			File targetFile = new File(targFileName);
			FileUtils.copyInputStreamToFile(in, targetFile);
			if ( mimeType == null ) {
				mimeType = Files.probeContentType(Paths.get(targetFile.toURI()));
				System.out.println("MIME Type 2 is "  + mimeType);
			}
			
			
			
			if ( mimeType == null  ) {
				
				BufferedInputStream bisCheck = new BufferedInputStream(new FileInputStream(targetFile));
				FileMagic fileValue = FileMagic.valueOf(bisCheck);
				System.out.println("File Value is " + fileValue);
				System.out.println("Doc is " + FileMagic.OLE2);
				System.out.println("Doc is " + FileMagic.OOXML);
				
				if ( fileValue!=null ) {
					mimeType = fileValue.toString();
				}
				
				
				System.out.println("MIME Type 3 is "  + mimeType);
			}
			
		
			if ( mimeType!=null ) {
				mimeType = mimeType.toLowerCase();
			} 
			
			System.out.println("MIME Type is "  + mimeType);
			
			//System.gc();
			
			if ( mimeType.contains( "pdf".toLowerCase() ) ) {
				
				System.out.println(fileCount + " PDF Size is " + targetFile.length());
				
//				FileInputStream fosTarg = new FileInputStream(targetFile);
				PdfReader reader = new PdfReader(targFileName);
//				copy.addDocument(reader);
				pdfCopy.addDocument(reader);
//				fosTarg.close();
//				in.close();
				reader.close();
				
			}
			else if ( mimeType.contains( "image".toLowerCase() ) ) {
				
				System.out.println("Not in Control Image" + mimeType);
				
				File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
				ImageConverter imageConverter = new ImageConverter(targetFile, tempFileK);
				imageConverter.convertToPdf();
				
				FileInputStream fosIn = new FileInputStream(tempFileK);
			    PdfReader reader = new PdfReader(fosIn);
//				copy.addDocument(reader);
				pdfCopy.addDocument(reader);
				fosIn.close();
				reader.close();
				
			}
			else if ( mimeType.contains( "eml".toLowerCase() ) || mimeType.contains( "message".toLowerCase() ) ) {
				
				System.out.println("Not in Control EML TYPE" + mimeType);
				
				File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
				EmlConvertor emlConvertor = new EmlConvertor(targetFile, tempFileK);
				emlConvertor.convertToPdf();
				
				FileInputStream fosIn = new FileInputStream(tempFileK);
			    PdfReader reader = new PdfReader(fosIn);
//				copy.addDocument(reader);
				pdfCopy.addDocument(reader);
				fosIn.close();
				reader.close();
				
			}  else if ( mimeType.contains( "msg".toLowerCase() ) || mimeType.contains( "x-ole-storage".toLowerCase() ) ) {
				
				System.out.println("Not in Control MSG TYPE" + mimeType);
				
				File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
				MsgConvertor msgConvertor = new MsgConvertor(targetFile, tempFileK);
				msgConvertor.convertToPdf();
				
				FileInputStream fosIn = new FileInputStream(tempFileK);
			    PdfReader reader = new PdfReader(fosIn);
//				copy.addDocument(reader);
				pdfCopy.addDocument(reader);
				fosIn.close();
				reader.close();
				
			}
			else {
				
				System.out.println("Not in Control Doc Else" + mimeType);
				
//				String fileExtension = getFileExtension(targetFile);
				String fileExtension = mergeFilePO.getFileExtensionType()!=null ? mergeFilePO.getFileExtensionType() : getFileExtension(targetFile);;
				if ( fileExtension!=null ) {
					fileExtension=fileExtension.toLowerCase();
				}
				System.out.println("File Extension is " + fileExtension);
				
				if ( fileExtension.endsWith("docx") || fileExtension.endsWith("doc") || fileExtension.endsWith("word_x") || fileExtension.endsWith("word") ) {
//				if ( mimeType.contains( "application/zip".toLowerCase() ) ) {
					
					File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
					WordConverter wordConverter = new WordConverter(targetFile, tempFileK);
					wordConverter.convertToPdf();
					
					FileInputStream fosIn = new FileInputStream(tempFileK);
				    PdfReader reader = new PdfReader(fosIn);
//					copy.addDocument(reader);
					pdfCopy.addDocument(reader);
					fosIn.close();
					reader.close();
			        
				} else if ( fileExtension.endsWith("xlsx") || fileExtension.endsWith("xls") || fileExtension.endsWith("excel_x") || fileExtension.endsWith("excel") ) {
//				} else if ( mimeType.contains( "application/zip".toLowerCase() ) ) {
					
					
					File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
					ExcelConverter excelConverter = new ExcelConverter(targetFile, tempFileK, fileExtension);
					excelConverter.convertToPdf();
					
					FileInputStream fosIn = new FileInputStream(tempFileK);
				    PdfReader reader = new PdfReader(fosIn);
//					copy.addDocument(reader);
					pdfCopy.addDocument(reader);
					fosIn.close();
					reader.close();
					
				}  else {
					
					File tempFileK = File.createTempFile( tempTargFileName, ".pdf");
					DefaultConverter defaultConverter = new DefaultConverter(targetFile, tempFileK);
					defaultConverter.convertToPdf();
				    
				    FileInputStream fosIn = new FileInputStream(tempFileK);
				    PdfReader reader = new PdfReader(fosIn);
//					copy.addDocument(reader);
					pdfCopy.addDocument(reader);
					fosIn.close();
					reader.close();
					
				}
				
			}
			
			
		}
		
		try {
//			copy.close();
//			copyDocument.close();
			pdfCopy.close();
		} catch (Exception e ) {
			e.printStackTrace();
		}
		

		
		try {

			
			boolean done = AmazonUtil.uploadS3FileWithUrl(tempFile.getAbsolutePath(), finalUrl);
			System.out.println("Upload " + done);
		} catch (Exception e) {
			System.out.println("Amazon Thread Exception" + e);
		}


	}
	
	
	/*private void mergePDFs(HttpURLConnection firstPDFConnection, HttpURLConnection secondPDFConnection,
			HttpServletResponse response) throws IOException, DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

//		File file = new File(attachmentRequest.getAttachmentName());

		// Create a PdfCopyFields object
		PdfCopyFields copy = new PdfCopyFields(baos);

		// Bad Password Exception if removed
		PdfReader.unethicalreading = true;

		// add a document
		PdfReader reader1 = new PdfReader(firstPDFConnection.getInputStream());
		copy.addDocument(reader1);

		// add a document
		PdfReader reader2 = new PdfReader(secondPDFConnection.getInputStream());
		copy.addDocument(reader2);

		// close the PdfCopyFields object
		copy.close();
		reader1.close();
		reader2.close();

//		saveAttachment(attachmentRequest.getParentId(), attachmentRequest.getAttachmentName(), baos, response);
		baos.flush();
		baos.close();
	}*/

}
