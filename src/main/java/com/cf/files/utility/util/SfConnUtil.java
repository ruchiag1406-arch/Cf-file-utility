package com.cf.files.utility.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.cf.files.utility.model.PdfSignRequest;
import com.cf.files.utility.model.RegisterModel;
import com.cf.files.utility.service.PropService;
import com.cf.files.utility.service.S3SignService;

public class SfConnUtil {

	public static final String SF_LOGIN_DEV_URL = "https://test.salesforce.com";
	public static final String SF_LOGIN_PROD_URL = "https://login.salesforce.com";

	public static final String USERNAME_DEV = "rahulaeran19@gmail.com.hpeprod.devlatest";
	public static final String USERNAME_QA = "rahulaeran19@gmail.com.hpeprod.uat2";
	public static final String USERNAME_PROD = "rahulaeran19@gmail.com.hpeprod";

	public static final String CONSUMER_KEY_DEV  = "3MVG9dCCPs.KiE4SymniyHC6UlBvaUt4oy9X1B_Q20iZv7jmTcc9Qkodt9sD9c_R8dq0V1s0ou_gi9euxSmRZ";
	public static final String CONSUMER_KEY_QA   = "3MVG9dzDZFnwTBRL2_PjD.38G22zz4.xadQiQFg.inRzqkN.w73Vc0WnCj.j4ogoQtxUmTJ1tznnvfHSuCVUx";
	public static final String CONSUMER_KEY_PROD = "3MVG9mclR62wycM3rM5AWEizLQjK7DKqwRIiuHm_kvqr1.jzxShnhjThPZV2Z7mIpAUc0SlgBqGbutEOnJFCF";

	public static String access_token_dev;
	public static String access_token_prod;
	
	public static long prevTimeDev = System.currentTimeMillis();
	public static long prevTimeQa = System.currentTimeMillis();
	public static long prevTimeProd = System.currentTimeMillis();
	
	public static String instance_url_dev;
	public static String instance_url_prod;
	
	public static void getTokenForDev(PdfSignRequest pdfSignRequest, RegisterModel rModel) {

		try {

			CloseableHttpClient client = HttpClients.createDefault();
			String baseUrl = SF_LOGIN_DEV_URL + "/services/oauth2/token";

			HttpPost oauthPost = new HttpPost(baseUrl);

			List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
			parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
			
			//RegisterModel rModel = new PropService().getData(pdfSignRequest.getOrgId());
			parametersBody.add(new BasicNameValuePair("assertion", getJWT(rModel.getClientConsumer(), rModel.getUserName(), SF_LOGIN_DEV_URL,rModel.getFilePath() )));
			oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, "UTF-8"));

			System.out.println("POST " + baseUrl + "...\n");
			HttpResponse response = client.execute(oauthPost);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200) {

				JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				access_token_dev = oauthLoginResponse.getString("access_token");
				instance_url_dev = oauthLoginResponse.getString("instance_url");
				pdfSignRequest.setInstanceUrl(instance_url_dev);
				pdfSignRequest.setAccessToken(access_token_dev);
				System.out.println("Dev Token is " + access_token_dev);
				System.out.println("Dev Url is " + instance_url_dev);

			} else {
				System.out.println("Dev Error: Token Response " + response.getStatusLine());
			}

		} catch (Exception e) {
			System.out.println("Dev Error: Not able to call Token service " + e.getMessage());
		}

		// Get user info.
		/*
		 * String userIdEndpoint = oauthLoginResponse.getString("id"); String
		 * accessToken = oauthLoginResponse.getString("access_token");
		 * List<BasicNameValuePair> qsList = new ArrayList<BasicNameValuePair>();
		 * qsList.add(new BasicNameValuePair("oauth_token", accessToken)); String
		 * queryString = URLEncodedUtils.format(qsList, "UTF-8"); HttpGet
		 * userInfoRequest = new HttpGet(userIdEndpoint + "?" + queryString);
		 * HttpResponse userInfoResponse = client.execute(userInfoRequest);
		 * 
		 * JSONObject userInfo = new
		 * JSONObject(EntityUtils.toString(userInfoResponse.getEntity()));
		 */

		/*
		 * Map<String, Object> userInfo = (Map<String, Object>) JSON
		 * .parse(EntityUtils.toString(userInfoResponse.getEntity()));
		 */
		/*
		 * System.out.println("User info response"); for (Map.Entry<String, Object>
		 * entry : userInfo.toMap().entrySet()) {
		 * System.out.println(String.format("  %s = %s", entry.getKey(),
		 * entry.getValue())); } System.out.println("");
		 * 
		 * // Use the user info in interesting ways. System.out.println("Username is " +
		 * userInfo.get("username")); System.out.println("User's email is " +
		 * userInfo.get("email")); Map<String, String> urls = (Map<String, String>)
		 * userInfo.toMap().get("urls"); System.out.println("REST API url is " +
		 * urls.get("rest").replace("{version}", "52.0") );
		 */
	}

	/*
	public static void getTokenForQa(PdfSignRequest pdfSignRequest) {

		try {

			CloseableHttpClient client = HttpClients.createDefault();
			String baseUrl = SF_LOGIN_QA_URL + "/services/oauth2/token";

			HttpPost oauthPost = new HttpPost(baseUrl);
			
			RegisterModel rModel = new PropService().getData(pdfSignRequest.getOrgId());
			List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
			parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
			parametersBody.add(new BasicNameValuePair("assertion", getJWT(rModel.getClientConsumer(), rModel.getUserName(), SF_LOGIN_QA_URL, rModel.getFilePath() )));
			oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, "UTF-8"));

			System.out.println("POST " + baseUrl + "...\n");
			HttpResponse response = client.execute(oauthPost);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200) {

				JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				access_token_qa = oauthLoginResponse.getString("access_token");
				instance_url_qa = oauthLoginResponse.getString("instance_url");
				pdfSignRequest.setInstanceUrl(instance_url_qa);
				pdfSignRequest.setAccessToken(access_token_qa);
				System.out.println("QA Token is " + access_token_qa);
				System.out.println("QA Url is " + instance_url_qa);

			} else {
				System.out.println("QA Error: Token Response " + response.getStatusLine());
			}

		} catch (Exception e) {
			System.out.println("QA Error: Not able to call Token service " + e.getMessage());
		}

	}
	*/

	public static void getTokenForProd(PdfSignRequest pdfSignRequest, RegisterModel rModel) {

		try {

			CloseableHttpClient client = HttpClients.createDefault();
			String baseUrl = SF_LOGIN_PROD_URL + "/services/oauth2/token";

			HttpPost oauthPost = new HttpPost(baseUrl);

			//RegisterModel rModel = new PropService().getData(pdfSignRequest.getOrgId());
			List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
			parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
			parametersBody.add(new BasicNameValuePair("assertion", getJWT(rModel.getClientConsumer(), rModel.getUserName(), SF_LOGIN_PROD_URL, rModel.getFilePath() )));
			oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, "UTF-8"));

			System.out.println("POST " + baseUrl + "...\n");
			HttpResponse response = client.execute(oauthPost);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200) {

				JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				access_token_prod = oauthLoginResponse.getString("access_token");
				instance_url_prod = oauthLoginResponse.getString("instance_url");
				pdfSignRequest.setInstanceUrl(instance_url_prod);
				pdfSignRequest.setAccessToken(access_token_prod);
				System.out.println("Prod Token is " + access_token_prod);
				System.out.println("Prod Instance is " + instance_url_prod);

			} else {
				System.out.println("Prod Error: Token Response " + response.getStatusLine());
			}

		} catch (Exception e) {
			System.out.println("Prod Error: Not able to call Token service " + e.getMessage());
		}

	}

	public static PdfSignRequest getUrlFromSf(String recordId, String org) {

		PdfSignRequest pdfSignRequest = new PdfSignRequest();
		pdfSignRequest.setRecordId(recordId);
		//pdfSignRequest.setOrg(org);
		pdfSignRequest.setOrgId(org);
		//pdfSignRequest.setBucketName(org);

		RegisterModel rModel = new PropService().getData(org);
		
		try {

			if ( rModel.isSandbox() ) {
				//if (access_token_dev == null || (System.currentTimeMillis() - prevTimeDev > (10 * 60 * 1000))) {
					getTokenForDev(pdfSignRequest, rModel);
					prevTimeDev = System.currentTimeMillis();
				/* } else {
					pdfSignRequest.setAccessToken(access_token_dev);
					pdfSignRequest.setInstanceUrl(instance_url_dev);
				} */
			} else {
				//if (access_token_prod == null || (System.currentTimeMillis() - prevTimeProd > (10 * 60 * 1000))) {
					getTokenForProd(pdfSignRequest,rModel);
					prevTimeProd = System.currentTimeMillis();
				/*} else {
					pdfSignRequest.setAccessToken(access_token_prod);
					pdfSignRequest.setInstanceUrl(instance_url_prod);
				}*/
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost uploadFile = new HttpPost(pdfSignRequest.getInstanceUrl() + "/services/apexrest/signRecordPdf");
			uploadFile.setHeader("Authorization", "OAuth " + pdfSignRequest.getAccessToken());

			JSONObject recordObj = new JSONObject();
			recordObj.put("recordId", recordId);
			JSONObject reqObj = new JSONObject();
			reqObj.put("req", recordObj);
			// reqObj.put("req", reqObj);

			StringEntity params = new StringEntity(reqObj.toString());
			uploadFile.addHeader("content-type", "application/json; charset=utf-8");
			uploadFile.setEntity(params);

			System.out
					.println("POST " + pdfSignRequest.getInstanceUrl() + "/services/apexrest/signRecordPdf" + "...\n");
			HttpResponse response = httpClient.execute(uploadFile);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200) {

				JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				System.out.println("Response is " + oauthLoginResponse);
				String amazonS3Url = oauthLoginResponse.optString("amazonS3Url", "");
				if ( "null".equals(amazonS3Url) ) {
					amazonS3Url = "";
				}
				String status = oauthLoginResponse.optString("status", "");
				if ( "null".equals(status) ) {
					status = "";
				}
				String sigName = oauthLoginResponse.optString("sigName", "");
				if ( "null".equals(sigName) ) {
					sigName = "";
				}
				String comments = oauthLoginResponse.optString("comments", "");
				if ( "null".equals(comments) ) {
					comments = "";
				}
				System.out.println("amazonS3Url is " + amazonS3Url);
				System.out.println("status is " + status);
				System.out.println("sigName is " + sigName);
				System.out.println("Comments is " + comments);
				pdfSignRequest.setStatus(status);
				pdfSignRequest.setSigName(sigName);
				pdfSignRequest.setComments(comments);
				pdfSignRequest.setAmazonS3Url(amazonS3Url);
				
				/* Added code 24 Jan 2021 to get new url before signing */
				S3SignService s3SignService  = new S3SignService();
				try {
					boolean expired =   s3SignService.checkExpiryNew(pdfSignRequest);
					if ( expired ) {
						String fileUrl = s3SignService.genUrlForPdfSign(amazonS3Url, recordId, org);
						System.out.println("new fileUrl for signing " + fileUrl);
						pdfSignRequest.setAmazonS3Url(fileUrl);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error: New url fetch " + e.getMessage());
				}
				/* Added code 24 Jan 2021 to get new url before signing */

			} else {
				System.out.println("Error: Fetch Url Response " + response.getStatusLine());
			}

		} catch (Exception e) {
			System.out.println("Error: Not able to get S3 Url " + e.getMessage());
		}

		return pdfSignRequest;

	}

	public static void saveStatusInSf(String recordId, String org, String signName, String signDate, String comments, String status) {

		PdfSignRequest pdfSignRequest = new PdfSignRequest();
		pdfSignRequest.setRecordId(recordId);
//		pdfSignRequest.setOrg(org);
		pdfSignRequest.setOrgId(org);
//		pdfSignRequest.setBucketName(org);

		try {
			
			RegisterModel rModel = new PropService().getData(org);

			if ( rModel.isSandbox() ) {
				//if (access_token_dev == null || (System.currentTimeMillis() - prevTimeDev > (10 * 60 * 1000))) {
					getTokenForDev(pdfSignRequest,rModel);
					prevTimeDev = System.currentTimeMillis();
				/* } else {
					pdfSignRequest.setAccessToken(access_token_dev);
					pdfSignRequest.setInstanceUrl(instance_url_dev);
				}*/
			} else {
				//if (access_token_prod == null || (System.currentTimeMillis() - prevTimeProd > (10 * 60 * 1000))) {
					getTokenForProd(pdfSignRequest, rModel);
					prevTimeProd = System.currentTimeMillis();
				/*} else {
					pdfSignRequest.setAccessToken(access_token_prod);
					pdfSignRequest.setInstanceUrl(instance_url_prod);
				}*/
			}
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost uploadFile = new HttpPost(pdfSignRequest.getInstanceUrl() + "/services/apexrest/signRecordPdf");
			uploadFile.setHeader("Authorization", "OAuth " + pdfSignRequest.getAccessToken());

			JSONObject recordObj = new JSONObject();
			recordObj.put("recordId", recordId);
			if ( "null".equals(status) ) {
				status = "";
			}
			recordObj.put("status", status);
			if ( "null".equals(signName) ) {
				signName = "";
			}
			recordObj.put("sigName", signName);
			if ( "null".equals(comments) ) {
				comments = "";
			}
			recordObj.put("comments", comments);
			
			recordObj.put("sigDate", signDate);
			JSONObject reqObj = new JSONObject();
			reqObj.put("req", recordObj);
			// reqObj.put("req", reqObj);

			StringEntity params = new StringEntity(reqObj.toString());
			uploadFile.addHeader("content-type", "application/json; charset=utf-8");
			uploadFile.setEntity(params);

			System.out
					.println("POST " + pdfSignRequest.getInstanceUrl() + "/services/apexrest/signRecordPdf" + "...\n");
			HttpResponse response = httpClient.execute(uploadFile);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200) {

				JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				status = oauthLoginResponse.getString("status");
				recordId = oauthLoginResponse.getString("recordId");

				System.out.println("status  is " + status);
				System.out.println("recordId  is " + recordId);

			} else {
				System.out.println("Error: Save Status Response " + response.getStatusLine());
			}

		} catch (Exception e) {
			System.out.println("Error: Not able to save Status " + e.getMessage());
		}

	}

	public static String getJWT(String consumer, String user, String url, String certFileName) {

		String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
		String claimTemplate = "'{'\"iss\": \"{0}\", \"sub\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";

		try {

			StringBuffer token = new StringBuffer();

			// Encode the JWT Header and add it to our string to sign
			token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));

			// Separate with a period
			token.append(".");

			// Create the JWT Claims Object
			String[] claimArray = new String[4];
			claimArray[0] = consumer;
			claimArray[1] = user;
			claimArray[2] = url;
			claimArray[3] = Long.toString((System.currentTimeMillis() / 1000) + 300);

			MessageFormat claims = new MessageFormat(claimTemplate);
			String payload = claims.format(claimArray);
			// System.out.println("Payload: " + payload);

			// Add the encoded claims object
			token.append(Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));

			// Load the private key from a keystore
			/*
			 * KeyStore keystore = KeyStore.getInstance("JKS"); keystore.load(new
			 * FileInputStream("./path/to/keystore.jks"), "keystorepassword".toCharArray());
			 * PrivateKey privateKey = (PrivateKey) keystore.getKey("certalias",
			 * "privatekeypassword".toCharArray());
			 */
			//PrivateKey privateKey = getPrivateKey("src/main/webapp/certserver.der");
			 PrivateKey privateKey = getPrivateKey(certFileName);
			 
			// Sign the JWT Header + "." + JWT Claims Object
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(privateKey);
			signature.update(token.toString().getBytes("UTF-8"));
			String signedPayload = Base64.encodeBase64URLSafeString(signature.sign());

			// Separate with a period
			token.append(".");

			// Add the encoded signature
			token.append(signedPayload);

			System.out.println("JWT Token: " + token.toString());
			return token.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static PrivateKey getPrivateKey(String filename) throws Exception {

		File f = new File(filename);
		System.out.println("Server File is " + f.getAbsolutePath());
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	/*
	 * public static void updateRecordInSf(String url, String sid) throws Exception
	 * {
	 * 
	 * 
	 * CloseableHttpClient httpClient = HttpClients.createDefault(); HttpPost
	 * uploadFile = new HttpPost(
	 * "https://horsepowr--devlatest.my.salesforce.com/services/apexrest/getObjectDetails"
	 * );
	 * 
	 * 
	 * int statusCode = httpClient.executeMethod(postMethod);
	 * 
	 * HttpPost uploadFile = new HttpPost(
	 * "https://horsepowr--devlatest.my.salesforce.com/services/apexrest/getObjectDetails"
	 * ); uploadFile.setHeader("Authorization", "OAuth " +
	 * "00D1k000000ET2O!AREAQB9e1dATTMbYUciUoWFd50AsD6lp_u_glWCxkin3vS2EMzmj22gKXIn_5hyCRIOK9TN7Ps7Yx4nkCIVUmdBpTv6Mt0Es"
	 * ); JSONObject attachment = new JSONObject(); attachment.put("recordId",
	 * "a0U1k000001gnnw");
	 * 
	 * StringRequestEntity requestEntity = new StringRequestEntity( JSON_STRING,
	 * "application/json", "UTF-8");
	 * 
	 * PostMethod postMethod = new PostMethod("http://example.com/action");
	 * postMethod.setRequestEntity(requestEntity);
	 * 
	 * StringEntity params = new StringEntity("{\"req\":" + "a0U1k000001gnnw" +
	 * "}"); uploadFile.addHeader("content-type",
	 * "application/json; charset=utf-8"); uploadFile.setEntity(params);
	 * 
	 * ArrayList postParameters = new ArrayList<BasicNameValuePair>();
	 * postParameters.add(new BasicNameValuePair("req", attachment.toString()));
	 * 
	 * 
	 * //uploadFile.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
	 * 
	 * MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	 * builder.addTextBody("req", attachment.toString(),
	 * ContentType.APPLICATION_JSON);
	 * 
	 * HttpEntity multipart = builder.build();
	 * 
	 * //uploadFile.setEntity(multipart); CloseableHttpResponse response =
	 * httpClient.execute(uploadFile); StringWriter writer = new StringWriter();
	 * IOUtils.copy(response.getEntity().getContent(), writer,
	 * StandardCharsets.UTF_8);
	 * 
	 * 
	 * //JSONObject jsonObject = new JSONObject(writer.toString());
	 * System.out.println("Upload code 1" + writer.toString() );
	 * 
	 * 
	 * }
	 * 
	 * private static class ApiError { public String errorCode; public String
	 * message; public String [] fields; }
	 */
	
	public static InputStream getFileStreamFromSF(String recordId, String objectType, String orgId) throws Exception {
		
		PdfSignRequest pdfSignRequest = new PdfSignRequest();
		pdfSignRequest.setRecordId(recordId);
		pdfSignRequest.setOrgId(orgId);

		RegisterModel rModel = new PropService().getData(orgId);
		
		if ( rModel.isSandbox() ) {
			//if (access_token_dev == null || (System.currentTimeMillis() - prevTimeDev > (10 * 60 * 1000))) {
				getTokenForDev(pdfSignRequest, rModel);
				prevTimeDev = System.currentTimeMillis();
			/*} else {
				pdfSignRequest.setAccessToken(access_token_dev);
				pdfSignRequest.setInstanceUrl(instance_url_dev);
			}*/
		} else {
			//if (access_token_prod == null || (System.currentTimeMillis() - prevTimeProd > (10 * 60 * 1000))) {
				getTokenForProd(pdfSignRequest, rModel);
				prevTimeProd = System.currentTimeMillis();
			 /*} else {
				pdfSignRequest.setAccessToken(access_token_prod);
				pdfSignRequest.setInstanceUrl(instance_url_prod);
			}*/ 
		}

		String SALESFORCE_PDF_FETCH_URL = null;
		if( "Attachment".equalsIgnoreCase(objectType) ){
			SALESFORCE_PDF_FETCH_URL = pdfSignRequest.getInstanceUrl() + "/services/data/v43.0/sobjects/" + objectType + "/" + recordId
				+ "/body";
		}else if( "ContentVersion".equalsIgnoreCase(objectType) ){
			SALESFORCE_PDF_FETCH_URL = pdfSignRequest.getInstanceUrl() + "/services/data/v43.0/sobjects/" + objectType + "/" + recordId
				+ "/VersionData";
		}
		
		System.out.println("SALESFORCE_PDF_FETCH_URL === " + SALESFORCE_PDF_FETCH_URL);
		URL url = new URL(SALESFORCE_PDF_FETCH_URL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Authorization", "OAuth " + pdfSignRequest.getAccessToken());
		System.out.println("Response Code " + con.getResponseCode());
        System.out.println("Response Message " + con.getResponseMessage() );
        /*for (String key : con.getHeaderFields().keySet() ) {
        	System.out.println("Header " + con.getHeaderField(key) );
        }*/
        /*for (String key : con.getRequestProperties().keySet() ) {
        	System.out.println("Request " + con.getRequestProperty(key) );
        }*/
		return con.getInputStream();

			
			
	}

	
	public static PdfSignRequest getFileUrlFromSf(String recordId, String org, String userId, String orgId) {
		
		PdfSignRequest pdfSignRequest = new PdfSignRequest();
		pdfSignRequest.setRecordId(recordId);
//		pdfSignRequest.setOrg(org);
//		pdfSignRequest.setBucketName(org);
		pdfSignRequest.setUserId(userId);
		pdfSignRequest.setOrgId(orgId);

		try {

			RegisterModel rModel = new PropService().getData(orgId);
			
			if ( rModel.isSandbox() ) {
				//if (access_token_dev == null || (System.currentTimeMillis() - prevTimeDev > (10 * 60 * 1000))) {
					getTokenForDev(pdfSignRequest, rModel);
					prevTimeDev = System.currentTimeMillis();
				/*} else {
					pdfSignRequest.setAccessToken(access_token_dev);
					pdfSignRequest.setInstanceUrl(instance_url_dev);
				}*/
			} else {
				//if (access_token_prod == null || (System.currentTimeMillis() - prevTimeProd > (10 * 60 * 1000))) {
					getTokenForProd(pdfSignRequest, rModel);
					prevTimeProd = System.currentTimeMillis();
				 /*} else {
					pdfSignRequest.setAccessToken(access_token_prod);
					pdfSignRequest.setInstanceUrl(instance_url_prod);
				}*/ 
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet uploadFile = new HttpGet(pdfSignRequest.getInstanceUrl() + "/services/data/v43.0/sobjects/Amazon_S3_Attachment__c/" + recordId + "?fields=Name,File_Url__c,Signed_Path__c,Expiration_Date__c");
			uploadFile.setHeader("Authorization", "OAuth " + pdfSignRequest.getAccessToken());

			//JSONObject recordObj = new JSONObject();
			//recordObj.put("recordId", recordId);
			//JSONObject reqObj = new JSONObject();
			//reqObj.put("req", recordObj);
			// reqObj.put("req", reqObj);

			//StringEntity params = new StringEntity(reqObj.toString());
			//uploadFile.addHeader("content-type", "application/json; charset=utf-8");
			//uploadFile.setEntity(params);

			System.out
					.println("POST " + pdfSignRequest.getInstanceUrl() + "/services/data/v43.0/sobjects/Amazon_S3_Attachment__c/" + recordId + "?fields=Name,File_Url__c,Signed_Path__c,Expiration_Date__c" + "\n");
			HttpResponse response = httpClient.execute(uploadFile);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200) {

				JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				String amazonS3Url = oauthLoginResponse.optString("File_URL__c", "");
				String signedS3Url = oauthLoginResponse.optString("Signed_Path__c", "");
				String expiryDate = oauthLoginResponse.optString("Expiration_Date__c", "");
				System.out.println("Date is " + expiryDate);
				System.out.println("amazonS3Url is " + amazonS3Url);
				
				
				
				String fullUrl = amazonS3Url;
				if ( !"null".equals(signedS3Url) ) {
					fullUrl = amazonS3Url+signedS3Url;
				} 
				pdfSignRequest.setAmazonS3Url(fullUrl);
				pdfSignRequest.setExpDate(expiryDate);

			} else {
				System.out.println("Error: Fetch Url Response " + response.getStatusLine());
			}

		} catch (Exception e) {
			System.out.println("Error: Not able to get S3 Url " + e.getMessage());
		}

		return pdfSignRequest;

	}
	
	public static boolean saveFileUrlInSf(String recordId, String org, String fileUrl) {
		
		PdfSignRequest pdfSignRequest = new PdfSignRequest();
		pdfSignRequest.setRecordId(recordId);
		//pdfSignRequest.setOrg(org);
		//pdfSignRequest.setBucketName(org);
		pdfSignRequest.setOrgId(org);

		try {
			
			RegisterModel rModel = new PropService().getData(org);

			if ( rModel.isSandbox() ) {
				//if (access_token_dev == null || (System.currentTimeMillis() - prevTimeDev > (10 * 60 * 1000))) {
					getTokenForDev(pdfSignRequest, rModel);
					prevTimeDev = System.currentTimeMillis();
				/* } else {
					pdfSignRequest.setAccessToken(access_token_dev);
					pdfSignRequest.setInstanceUrl(instance_url_dev);
				} */
			} else {
				if (access_token_prod == null || (System.currentTimeMillis() - prevTimeProd > (10 * 60 * 1000))) {
					getTokenForProd(pdfSignRequest, rModel);
					prevTimeProd = System.currentTimeMillis();
				 } else {
					pdfSignRequest.setAccessToken(access_token_prod);
					pdfSignRequest.setInstanceUrl(instance_url_prod);
				}
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPatch uploadFile = new HttpPatch(pdfSignRequest.getInstanceUrl() + "/services/data/v43.0/sobjects/Amazon_S3_Attachment__c/" + recordId);
			uploadFile.setHeader("Authorization", "OAuth " + pdfSignRequest.getAccessToken());
			
			JSONObject recordObj = new JSONObject();
			
			/* Fix added */
			int xIdx = fileUrl.indexOf("?X-Amz");
			if ( xIdx > 0  ) {
				String f1Url = fileUrl.substring(0, xIdx);
				String f2Url = fileUrl.substring(xIdx);
				System.out.println("Full url is " + fileUrl);
				System.out.println("F1 url is " + f1Url);
				System.out.println("F2 url is " + f2Url);
				recordObj.put("File_URL__c", f1Url);
				recordObj.put("Signed_Path__c", f2Url);
			} else {
				System.out.println("Full other url is " + fileUrl);
				recordObj.put("File_URL__c", fileUrl);
			}
			
        	/* int accIdx = fileUrl.indexOf("AWSAccessKeyId");
			int exIdx = fileUrl.indexOf("Expires=");
			if ( accIdx > 0  ) {
				String fUrl = fileUrl.substring(0, accIdx) + fileUrl.substring(exIdx);
				System.out.println("Fixed url is " + fUrl);
				recordObj.put("File_URL__c", fUrl);
			}
			*/
			
			long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60 * 24 * 7;
            Date date = new Date(expTimeMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = dateFormat.format(date);  
			System.out.println("Expiration str date " + strDate);
			
			//2014-11-20T14:23:44.000+0000
			//2021-10-29T07:47:02.000+0000
			recordObj.put("Expiration_Date__c", (strDate + "T07:47:02.000+0000"));
			//JSONObject reqObj = new JSONObject();
			//reqObj.put("req", recordObj);
			// reqObj.put("req", reqObj);
			System.out.println("PArams " + recordObj.toString());

			StringEntity params = new StringEntity(recordObj.toString(), ContentType.APPLICATION_JSON);
			uploadFile.addHeader("Accept", "application/json");
			uploadFile.addHeader("Content-Type", "application/json; charset=utf-8");
			uploadFile.setEntity(params);
			System.out.println("PArams " + params);

			System.out
					.println("POST Save File Url " + pdfSignRequest.getInstanceUrl() + "/services/data/v43.0/sobjects/Amazon_S3_Attachment__c/" + recordId + "\n");
			HttpResponse response = httpClient.execute(uploadFile);
			int code = response.getStatusLine().getStatusCode();

			if (code == 204) {

				//JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				System.out.println(" Save File Url Response " + response.toString());
				return true;
				/* String amazonS3Url = oauthLoginResponse.optString("File_URL__c", "");
				
				System.out.println("amazonS3Url is " + amazonS3Url);
				
				
				pdfSignRequest.setAmazonS3Url(amazonS3Url); */

			} else {
				System.out.println("Error: Save File Url Response " + response.getStatusLine());
			}

		} catch (Exception e) {
			System.out.println("Error: Not able to save file url " + e.getMessage());
			e.printStackTrace();
		}

		return false;

	}

}
