package com.cf.files.utility.amazon;

import java.io.File;

import com.cf.files.utility.model.AttachmentRequestPO;

/**
 * @author raghav.simlote
 *
 */
public class AmazonUtil {
	
	private static final AmazonClient amazonClient = new AmazonClient();
	
    public static AmazonObj uploadFile( final File file, AttachmentRequestPO attachmentRequest) {//final String recordId) {
    	
//    	return amazonClient.uploadFile(file, recordId);
    	return amazonClient.uploadFile(file, attachmentRequest);
    	
    }
    
    public static AmazonObj uploadFileWithUrl( final File file, AttachmentRequestPO attachmentRequest) {//final String recordId) {
    	
//    	return amazonClient.uploadFile(file, recordId);
    	return amazonClient.uploadFileWithUrl(file, attachmentRequest);
    	
    }
    
    public static boolean uploadS3File( String fileStr, String s3Url) { //final String recordId) {
    	
    	return amazonClient.uploadS3File(fileStr, s3Url);
    	
    }
    
    public static boolean uploadS3FileWithUrl( String fileStr, String s3Url) {
    	
    	//return amazonClient.uploadS3File(fileStr, s3Url);
    	return amazonClient.uploadS3FileWithUrl(fileStr, s3Url);
    	
    }

    public static void deleteFile(final String fileUrl) {
    	
    	amazonClient.deleteFileFromS3Bucket(fileUrl);
    	
    }
    
    public static String generateFileUrl( final String buckName, final String fileName) {//final String recordId) {
    	
//    	return amazonClient.uploadFile(file, recordId);
    	return amazonClient.generateSignedUrl(buckName, fileName);
    	
    }

}
