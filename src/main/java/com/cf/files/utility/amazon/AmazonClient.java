package com.cf.files.utility.amazon;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.cf.files.utility.amazon.service.AmazonService;
import com.cf.files.utility.model.AttachmentRequestPO;
import com.cf.files.utility.util.Constants;
import com.cf.files.utility.util.EncodingUtil;

/**
 * @author raghav.simlote
 *
 */
public class AmazonClient {

    private static AmazonS3 s3client;
    
    static {
    	
    	/*
    	AWSCredentials credentials = new BasicAWSCredentials(Constants.ACCESS_KEY, Constants.SECRET_KEY);
        this.s3client = new AmazonS3Client(credentials);
        */
    	
    	ClientConfiguration config = new ClientConfiguration();
    	config.setSignerOverride("S3SignerType");
    	 //config.setProtocol(Protocol.HTTP);
    	    
    	
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(Constants.NEW_ACCESS_KEY, Constants.NEW_SECRET_KEY);
        s3client = AmazonS3ClientBuilder.standard()
                                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                                .withRegion("us-east-1")
                                //.withClientConfiguration(config)
                                .build();
    	
    }
    
    public AmazonClient() {
    	
    	
        
    }

//    private void initializeAmazon() {
//       AWSCredentials credentials = new BasicAWSCredentials(Constants.ACCESS_KEY, Constants.SECRET_KEY);
//       this.s3client = new AmazonS3Client(credentials);
//    }
    
    public AmazonObj uploadFile(final File multipartFile, AttachmentRequestPO attachmentRequest) {//final String recordId) {
        
    	AmazonObj amazonObj = new AmazonObj();
    	String fileUrl = "";
        try {
//            final File file = multipartFile;
        	String version = "" + new Date().getTime();
        	amazonObj.setVersion(version);
//            final String fileName = generateFileName(multipartFile);
//        	return version + "/" + multiPart.getName().replace(" ", "_");
        	
        	String recordId = attachmentRequest.getParentId();
        	final String orgBucketName = attachmentRequest.getOrgBucketName() ;
        	final String fileName;
        	
        	if ( attachmentRequest.getBaseUrl() != null ) {
//        		orgBucketName = attachmentRequest.getOrgBucketName();
        		fileName =  attachmentRequest.getBaseUrl()   + "/" + multipartFile.getName();
        	} else {
        		/*if ( attachmentRequest.getOrgBucketName() != null && attachmentRequest.getBucketName() != null && attachmentRequest.getsObjectName() != null) {
//           		 orgBucketName = attachmentRequest.getOrgBucketName(); */
           		 fileName = attachmentRequest.getsObjectName() + "/" + recordId + "/" + multipartFile.getName();
           		/* }  else {
        			orgBucketName = Constants.BUCKET_NAME;
        			fileName = recordId + "/" + multipartFile.getName();
        		}*/
        	}
        	
        	
        	
        	//final String fileName = subBucketName + "/" + recordId + "/" + version + "/" + multipartFile.getName();
            //fileUrl = Constants.END_POINT_URL + "/" + orgBucketName + "/" + fileName;
        	fileUrl = "https://" + orgBucketName + Constants.S3_URL + "/" + fileName;
            
        	amazonObj.setFileUrl(fileUrl);
            
            new Thread(new Runnable() {
    			
    			public void run() {
    				// TODO Auto-generated method stub
    				uploadFileTos3bucket(orgBucketName, fileName, multipartFile);
    				multipartFile.delete();
    			}
    		}).start(); 
            
            
        } catch (Exception e) {
           e.printStackTrace();
        }
        System.out.println("File Url is " + fileUrl);
        return amazonObj;
    }
    
    public boolean uploadS3File(String fileStr, String s3Url) {
    	
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
        	
            new Thread(new Runnable() {
    			
    			public void run() {
    				// TODO Auto-generated method stub
    				uploadFileTos3bucket(buckName, fileName, multipartFile);
    				try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						System.out.println("System sleep not done");
					}
//    				multipartFile.delete();
    			}
    		}).start(); 
            
            
        } catch (Exception e) {
        	isDone = false;
           e.printStackTrace();
        }
       
        return isDone;
        
    }
    
    public boolean uploadS3FileWithUrl(String fileStr, String s3Url) {
        
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
    				uploadFileTos3Url(buckName, fName, multipartFile);
    				try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						System.out.println("System sleep not done");
					}
//    				multipartFile.delete();
    			}
    		}).start(); 
            
            
        } catch (Exception e) {
        	isDone = false;
           e.printStackTrace();
        }
       
        return isDone;
    }

//    private File convertMultiPartToFile(File file) throws IOException {
//        File convFile = new File(file.getOriginalFilename());
//        FileOutputStream fos = new FileOutputStream(convFile);
//        fos.write(file.getBytes());
//        fos.close();
//        return convFile;
//    }

//    private String generateFileName(File multiPart, String version) {
//        return version + "/" + multiPart.getName().replace(" ", "_");
//    }

    public void uploadFileTos3bucket(String orgBucketName, String fileName, File file) {
        s3client.putObject(new PutObjectRequest(orgBucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
    
    public void uploadFileTos3Url(String orgBucketName, String fileName, File file) {
    	PutObjectResult result = s3client.putObject(new PutObjectRequest(orgBucketName, fileName, file));
    	System.out.println("File Upload result is " + result.getETag());
    }
    
    public String getFileUploadUrl(String orgBucketName, String objectKey) {
    	
    	// Set the presigned URL to expire after one week.
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += 1000 * 60 * 60 * 24 * 7;
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        System.out.println("Generating pre-signed URL.");
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(orgBucketName, objectKey)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);
        URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toExternalForm();
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
    	
        final String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        
        new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				s3client.deleteObject(new DeleteObjectRequest(Constants.BUCKET_NAME, fileName));
			}
		}).start();  
        
        return "Successfully deleted";
        
    }
    
    public String generateSignedUrl(String bucketName, String objectKey) {
    	//Regions clientRegion = Regions.DEFAULT_REGION;
        //String bucketName = "*** Bucket name ***";
        //String objectKey = "*** Object key ***";

        try {
           
            // Set the presigned URL to expire after one hour.
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60 * 24 * 7;
            expiration.setTime(expTimeMillis);
            
            // Generate the pre-signed URL.
            System.out.println("Generating pre-signed URL.");
            //changed 27 may 2022
            /*GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);*/
            //URL url1 = s3client.generatePresignedUrl(bucketName, objectKey, expiration, HttpMethod.GET);
            
            URL url = new AmazonService().generateFileUrl(bucketName, objectKey, expiration);
            
            System.out.println("Pre-Signed URL1 : " + url.toString());
            System.out.println("Pre-Signed URL2 : " + url.toExternalForm());
            return url.toExternalForm();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean copyFile(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) {
    	

        try {
           
           
            AccessControlList acl = s3client.getObjectAcl(sourceBucketName, sourceKey);
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey)
            		.withAccessControlList(acl);
            CopyObjectResult res = s3client.copyObject(copyObjectRequest);
//            s3client.deleteObject(sourceBucketName, sourceKey);
            //CopyObjectResult res = s3client.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
            if ( res!=null ) {
            	return true;
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean copyFileNoACL(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) throws Exception{
    	
        try {
        	
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
            CopyObjectResult res = s3client.copyObject(copyObjectRequest);
//            s3client.deleteObject(sourceBucketName, sourceKey);
            //CopyObjectResult res = s3client.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
            if ( res!=null ) {
            	return true;
            }
           
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        return false;
    }
    
    public boolean objKeyExists(String sourceBucketName, String sourceKey) throws Exception{
    	
        try {
        	
            //CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
            boolean res = s3client.doesObjectExist(sourceBucketName, sourceKey);
//            s3client.deleteObject(sourceBucketName, sourceKey);
            //CopyObjectResult res = s3client.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
            if ( res ) {
            	return true;
            }
           
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        return false;
    }
    
    public boolean delFile(String sourceBucketName, String sourceKey) {
    	
        try {
            
            s3client.deleteObject(sourceBucketName, sourceKey);
            return true;
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    
    public AmazonObj uploadFileWithUrl(final File multipartFile, AttachmentRequestPO attachmentRequest) {//final String recordId) {
        
    	AmazonObj amazonObj = new AmazonObj();
    	String fileUrl = "";
        try {
//            final File file = multipartFile;
        	String version = "" + new Date().getTime();
        	amazonObj.setVersion(version);
//            final String fileName = generateFileName(multipartFile);
//        	return version + "/" + multiPart.getName().replace(" ", "_");
        	
        	String recordId = attachmentRequest.getParentId();
        	final String orgBucketName = attachmentRequest.getOrgBucketName() ;
        	final String fileName;
        	
        	if ( attachmentRequest.getBaseUrl() != null ) {
//        		orgBucketName = attachmentRequest.getOrgBucketName();
        		
        		//fileName =  attachmentRequest.getBaseUrl()   + "/" + multipartFile.getName();
        		/* Fix added 29-11-21 */
            	String dUrl = EncodingUtil.getDecodedStr(attachmentRequest.getBaseUrl());
    			System.out.println("Decoded Base Url in Merge " + dUrl);
        		fileName =  dUrl   + "/" + multipartFile.getName();
        	} else {
        		/*if ( attachmentRequest.getOrgBucketName() != null && attachmentRequest.getBucketName() != null && attachmentRequest.getsObjectName() != null) {
//           		 orgBucketName = attachmentRequest.getOrgBucketName(); */
           		 fileName = attachmentRequest.getsObjectName() + "/" + recordId + "/" + multipartFile.getName();
           		/* }  else {
        			orgBucketName = Constants.BUCKET_NAME;
        			fileName = recordId + "/" + multipartFile.getName();
        		}*/
        	}
        	
        	
        	//final String fileName = subBucketName + "/" + recordId + "/" + version + "/" + multipartFile.getName();
            //fileUrl = Constants.END_POINT_URL + "/" + orgBucketName + "/" + fileName;
        	//fileUrl = "https://" + orgBucketName + Constants.S3_URL + "/" + fileName;
            
        	/* */
        	//fileUrl = getFileUploadUrl(orgBucketName, fileName);
        	System.out.println("Going to generate signed url for merge " + attachmentRequest.getOrgId());
        	fileUrl = generateSignedUrl(attachmentRequest.getOrgId(), fileName);
        	
        	/* Fix added */
        	int xIdx = fileUrl.indexOf("?X-Amz");
			if ( xIdx > 0  ) {
				String f1Url = fileUrl.substring(0, xIdx);
				String f2Url = fileUrl.substring(xIdx);
				System.out.println("M Full url is " + fileUrl);
				System.out.println("M F1 url is " + f1Url);
				System.out.println("M F2 url is " + f2Url);
				amazonObj.setFileUrl(f1Url);
				amazonObj.setSignedPath(f2Url);
				
			} else {
				System.out.println("Full other url is " + fileUrl);
				amazonObj.setFileUrl(fileUrl);
			}
        	
			/*
        	int accIdx = fileUrl.indexOf("AWSAccessKeyId");
			int exIdx = fileUrl.indexOf("Expires=");
			if ( accIdx > 0  ) {
				fileUrl = fileUrl.substring(0, accIdx) + fileUrl.substring(exIdx);
				System.out.println("Fixed url is " + fileUrl);
			}
			*/
        	
        	System.out.println("Heroku Merge File Upload Url " + fileUrl);
        	
            
            new Thread(new Runnable() {
    			
    			public void run() {
    				// TODO Auto-generated method stub
    				//uploadFileTos3Url(orgBucketName, fileName, multipartFile);
    				new AmazonService().uploadFileTos3Url(attachmentRequest.getOrgId(), fileName, multipartFile);
    				multipartFile.delete();
    			}
    		}).start(); 
            
            
        } catch (Exception e) {
           e.printStackTrace();
        }
        System.out.println("File Url is " + fileUrl);
        return amazonObj;
    }
    
    
}
