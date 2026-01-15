package com.cf.files.utility.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import com.cf.files.utility.model.Auth;
import com.cf.files.utility.model.RegisterModel;
import com.cf.files.utility.util.AuthUtils;

public class SfService {
	
	public void saveRecord(RegisterModel rModel) {
		
		
		
		try {
		
			AuthUtils authUtils=new AuthUtils();
			String org = "dev";
			if ( !rModel.isSandbox() ) {
				org = "prod";
			}
			Auth auth=authUtils.getConnectionDetail(org, rModel);

			CloseableHttpClient httpClient = HttpClients.createDefault();
			String objName = "EC2_Setting__c";
			String nameSpace = rModel.getNameSpace();
			System.out.println("NameSpace is " + nameSpace);
			if ( nameSpace!=null ) {
				if ( ! ( nameSpace.isEmpty() || nameSpace.equals("null") ) ) {
					objName = nameSpace + objName;
				}
				
			}
			HttpPost uploadFile = new HttpPost(
					auth.getInstanceUrl() + "/services/data/v43.0/sobjects/" + objName + "/");
			uploadFile.setHeader("Authorization", "OAuth " + auth.getAccessToken());

			JSONObject recordObj = new JSONObject();
			
			recordObj.put("File_URL_Generate_Service__c", rModel.getGenerateService());
			recordObj.put("Merge_File_Generate_Service__c", rModel.getMergeService());
			recordObj.put("MicroService_Base_URL__c", rModel.getBaseUrl());
			recordObj.put("S3_Bucket_Name__c", rModel.getBucketName());
			recordObj.put("S3_Host__c", rModel.getS3Host());
			recordObj.put("S3_Key__c", rModel.getAwsKey());
			recordObj.put("S3_Secret__c", rModel.getAwsSecret());
			recordObj.put("Sign_Service__c", rModel.getSignService());
		

			// JSONObject reqObj = new JSONObject();
			// reqObj.put("req", recordObj);
			// reqObj.put("req", reqObj);
			System.out.println("Field Params " + recordObj.toString());

			StringEntity params = new StringEntity(recordObj.toString(), ContentType.APPLICATION_JSON);
			uploadFile.addHeader("Accept", "application/json");
			uploadFile.addHeader("Content-Type", "application/json; charset=utf-8");
			uploadFile.setEntity(params);
			System.out.println("Meta Params " + params);

			System.out.println("POST Save File Url " + auth.getInstanceUrl()
					+ "/services/data/v43.0/sobjects/" + objName + "/");
			HttpResponse response = httpClient.execute(uploadFile);
			int code = response.getStatusLine().getStatusCode();

			if (code == 204) {

				// JSONObject oauthLoginResponse = new
				// JSONObject(EntityUtils.toString(response.getEntity()));
				System.out.println("Save Error Log Response " + response.toString());
				
				/*
				 * String amazonS3Url = oauthLoginResponse.optString("File_URL__c", "");
				 * 
				 * System.out.println("amazonS3Url is " + amazonS3Url);
				 * 
				 * 
				 * pdfSignRequest.setAmazonS3Url(amazonS3Url);
				 */

			} else {
				System.out.println("Error: Save Custom Setting Response " + response.getStatusLine());
			}

		} catch (Exception e) {
			System.out.println("Error: Not able to Save Custom Setting " + e.getMessage());
			e.printStackTrace();
		}
		
		
	}

}
