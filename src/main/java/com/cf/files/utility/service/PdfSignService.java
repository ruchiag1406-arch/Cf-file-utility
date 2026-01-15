package com.cf.files.utility.service;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cf.files.utility.amazon.util.AmazonNewUtil;
import com.cf.files.utility.model.PdfCord;
import com.cf.files.utility.model.PdfSignRequest;
import com.cf.files.utility.util.FetchingUtil;
import com.cf.files.utility.util.PdfUtility;
import com.cf.files.utility.util.SfConnUtil;

public class PdfSignService {
	
	public void getUnsignPdfData(String recordId, String org) throws Exception  {
		
		String s3url = null;
		JSONArray jsonArr = new JSONArray();
		File useFile = null;
		PdfSignRequest pdfReq = null;
		PdfCord pdfCord = new PdfCord();
		boolean signed = true;
		
		if ( recordId.contains(",") ) {
			
			String [] recArr = recordId.split(","); 
			
			int i=0;
			
			while (i<recArr.length) {
				
				JSONObject jsonObj = new JSONObject();
				
				pdfReq = SfConnUtil.getUrlFromSf(recArr[i], org);
				s3url = pdfReq.getAmazonS3Url();
//				System.out.println("S3 URL is " + s3url);
				
				File tempFile = File.createTempFile("src/main/webapp/unsigned", ".pdf");
				useFile = new File("src/main/webapp/"+tempFile.getName());
				
				InputStream in = FetchingUtil.getPDFFile(s3url);
				FileUtils.copyInputStreamToFile(in, useFile);
				in.close();
				
				try {
//					PdfUtility.applyCoord( getServletContext().getRealPath(SRC) );
					pdfCord = PdfUtility.applyCoord( useFile.getAbsolutePath()) ;
				} catch (Exception e) {	
					e.printStackTrace();
				}
				
				
				jsonObj.put("recordId", recArr[i]);
				jsonObj.put("org", org);
				jsonObj.put("sigName", pdfReq.getSigName());
				String comments = pdfReq.getComments();
				comments = comments.replaceAll("'", "%s1%");
				comments = comments.replaceAll("\"", "%s2%");
				jsonObj.put("comments", pdfReq.getComments());
				jsonObj.put("s3Url", s3url);
				jsonObj.put("fileName", useFile.getName());
				jsonObj.put("isNew", "false");
				if ( "Signed".equals(pdfReq.getStatus()) ) {
					 jsonObj.put("isSign", "true");
				} else {
					jsonObj.put("isSign", "false");
					signed = false;
				}
				
				
				jsonObj.put("s1", pdfCord.getxSign());
				jsonObj.put("s2", pdfCord.getySign());
				jsonObj.put("n1", pdfCord.getxName());
				jsonObj.put("n2", pdfCord.getyName());
				jsonObj.put("d1", pdfCord.getxDate());
				jsonObj.put("d2", pdfCord.getyDate());
				jsonObj.put("c1", pdfCord.getxComm());
				jsonObj.put("c2", pdfCord.getyComm());
				jsonObj.put("page", pdfCord.getPage()-1);
				
				jsonArr.put(jsonObj);
				i++;
				
			}
			
		} else {
			
			pdfReq = SfConnUtil.getUrlFromSf(recordId, org);
			s3url = pdfReq.getAmazonS3Url();
//			System.out.println("S3 URL is " + s3url);
			
			File tempFile = File.createTempFile("src/main/webapp/unsigned", ".pdf");
			useFile = new File("src/main/webapp/"+tempFile.getName());
			
			InputStream in = FetchingUtil.getPDFFile(s3url);
			FileUtils.copyInputStreamToFile(in, useFile);
			in.close();
			
			try {
//				PdfUtility.applyCoord( getServletContext().getRealPath(SRC) );
				pdfCord = PdfUtility.applyCoord( useFile.getAbsolutePath()) ;
			} catch (Exception e) {	
				e.printStackTrace();
			}
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("recordId", recordId);
			jsonObj.put("org", org);
			jsonObj.put("sigName", pdfReq.getSigName());
			jsonObj.put("comments", pdfReq.getComments());
			jsonObj.put("s3Url", s3url);
			jsonObj.put("fileName", useFile.getName());
			jsonObj.put("isNew", "false");
			if ( "Signed".equals(pdfReq.getStatus()) ) {
				 jsonObj.put("isSign", "true");
			} else {
				jsonObj.put("isSign", "false");
				signed = false;
			}
			
			jsonObj.put("s1", pdfCord.getxSign());
			jsonObj.put("s2", pdfCord.getySign());
			jsonObj.put("n1", pdfCord.getxName());
			jsonObj.put("n2", pdfCord.getyName());
			jsonObj.put("d1", pdfCord.getxDate());
			jsonObj.put("d2", pdfCord.getyDate());
			jsonObj.put("c1", pdfCord.getxComm());
			jsonObj.put("c2", pdfCord.getyComm());
			jsonObj.put("page", pdfCord.getPage()-1);
			
			jsonArr.put(jsonObj);
		}
		
		
	}
	
	public boolean processSignReq(JSONArray jsonArr, String signData) throws Exception {
		
		boolean signed = true;
		
		Date date1 = new Date();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		final String signDate = format1.format(date1);
		format1 = new SimpleDateFormat("MM/dd/yy");
		String forSignDate = format1.format(date1);
		
		System.out.println("Sign Image is there");
		
//		JSONArray jsonArr = new JSONArray(jsonStr);
		System.out.println("Json is " + jsonArr);
		
		for ( int i=0; i<jsonArr.length(); i++) {
			
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			
			String nam  = jsonObj.getString("sigName");
			nam = new String(Base64.getDecoder().decode(nam), StandardCharsets.UTF_8);
			String comm = jsonObj.getString("comments");
			comm = new String(Base64.getDecoder().decode(comm), StandardCharsets.UTF_8);
			System.out.println("Comments 1 is " + comm);
			
//			final String signName = jsonObj.getString("sigName");
			final String signName = nam;
//			final String comments = jsonObj.getString("comments");
			final String comments = comm;
			
//			nam = nam.replaceAll("'", "%s1%");
			nam = Base64.getEncoder().encodeToString(nam.getBytes(StandardCharsets.UTF_8));
			System.out.println("Name is " + nam);
			jsonObj.put("sigName", nam);
			
//			comm = comm.replaceAll("'", "%s1%");
			comm = Base64.getEncoder().encodeToString(comm.getBytes(StandardCharsets.UTF_8));
			System.out.println("Comments 2 is " + comm);
			jsonObj.put("comments", comm);
			
			String signStr  = jsonObj.getString("isSign");
			if ( "false".equals(signStr) ) {
				signed = false;
				continue;
			}
			
			String newStr  = jsonObj.getString("isNew");
			if ( "false".equals(newStr) ) {
				continue;
			}
			
			
			final String fileName = jsonObj.getString("fileName");
			final String recordId = jsonObj.getString("recordId");
			final String org = jsonObj.getString("org");
			//final String orgId = jsonObj.getString("orgId");
			final String s3Url = jsonObj.getString("s3Url");
			
			System.out.println("Record Id for Sign " + recordId);
//			System.out.println("org Id for Sign " + org);
//			System.out.println("S3 Url for Sign " + s3Url);
			System.out.println("org Id for Sign " + org);
			
			File tempFile = File.createTempFile("signed", ".pdf");
			final File useFile = new File("src/main/webapp/"+tempFile.getName());
			final File sFile = new File("src/main/webapp/"+fileName);
			
		
			
			if ( signData.equals("DeclinedtoSign") ) {
				try {
					
					PdfUtility.createSignedPdf(sFile.getAbsolutePath(), useFile.getAbsolutePath(), null, signName, forSignDate, comments);
//					AmazonUtil.uploadS3File(useFile.getAbsolutePath(), s3Url);
					
//					changed 27 may 2022
//					AmazonUtil.uploadS3FileWithUrl(useFile.getAbsolutePath(), s3Url);
					AmazonNewUtil.uploadS3FileWithUrl(useFile.getAbsolutePath(), s3Url, org);
					
					new Timer().schedule(
						    new TimerTask() {
						        @Override
						        public void run() {
						        	System.out.println("Declined right 10 secs");
									SfConnUtil.saveStatusInSf(recordId, org, signName, signDate, comments, "Declined to Sign");
						        }
						    }, 
						    5000
						);
					
					/* new Thread(new Runnable() {
						
						public void run() {
							
						}
					}).start(); */
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}			
			} else {
				try {
					PdfUtility.createSignedPdf(sFile.getAbsolutePath(), useFile.getAbsolutePath(), signData, signName, forSignDate, comments);
//					AmazonUtil.uploadS3File(useFile.getAbsolutePath(), s3Url);
					
//					changed 27 may 2022
//					AmazonUtil.uploadS3FileWithUrl(useFile.getAbsolutePath(), s3Url);
					AmazonNewUtil.uploadS3FileWithUrl(useFile.getAbsolutePath(), s3Url, org);
					
					new Timer().schedule(
						    new TimerTask() {
						        @Override
						        public void run() {
						            System.out.println("Running Signing after 10 secs");
						            SfConnUtil.saveStatusInSf(recordId, org, signName, signDate, comments, "Signed");
						        }
						    }, 
						    5000
						);
					
					/* new Thread(new Runnable() {
						
						public void run() {
							
						}
					}).start(); */
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
			//need to check, this is not for decline sign
			jsonObj.put("fileName", useFile.getName());
			jsonObj.put("isNew", "false");
			
			
		}
		
		return signed;
		
		
		
	}
	
	public void processDeclineRequest(String recordId, String org) throws Exception  {
		
		
		
	}
	
	public void clearFiles() {
		
//		System.out.println( "Current Time " + System.currentTimeMillis());
		File directory = new File("src/main/webapp/");
		if ( directory.isDirectory() ) {
			for (File f : directory.listFiles()) {
				String name = f.getName();
				if ( name.contains("signed") ) {
					if ( System.currentTimeMillis() - f.lastModified() > 30*60*1000 ) {
						f.delete();
						System.out.println( "Time " + f.lastModified());
					}
					
				}
			}
		}
		
	}

}
