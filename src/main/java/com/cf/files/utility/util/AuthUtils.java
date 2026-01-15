package com.cf.files.utility.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.cf.files.utility.model.Auth;
import com.cf.files.utility.model.RegisterModel;

public class AuthUtils {

    public static final String SF_LOGIN_DEV_URL = "https://test.salesforce.com";
    //public static final String SF_LOGIN_QA_URL = "https://test.salesforce.com";
    public static final String SF_LOGIN_PROD_URL = "https://login.salesforce.com";

    public static final String USERNAME_DEV = "rahulaeran19@gmail.com.hpeprod.devlatest";
    //public static final String USERNAME_QA = "rahulaeran19@gmail.com.hpeprod.uat2";
    public static final String USERNAME_PROD = "rahulaeran19@gmail.com.hpeprod";

    public static final String PASSWORD_SECURITY_TOKEN_DEV = "R1234567LZarA2LoV6L0FWmdOTLvkfZh";

    public static final String CONSUMER_KEY_DEV  = "3MVG9dCCPs.KiE4SymniyHC6UlBvaUt4oy9X1B_Q20iZv7jmTcc9Qkodt9sD9c_R8dq0V1s0ou_gi9euxSmRZ";
    //public static final String CONSUMER_KEY_QA   = "3MVG9dzDZFnwTBRL2_PjD.38G22zz4.xadQiQFg.inRzqkN.w73Vc0WnCj.j4ogoQtxUmTJ1tznnvfHSuCVUx";
    public static final String CONSUMER_KEY_PROD = "3MVG9mclR62wycM3rM5AWEizLQjK7DKqwRIiuHm_kvqr1.jzxShnhjThPZV2Z7mIpAUc0SlgBqGbutEOnJFCF";

    public static final String CONSUMER_SECRET_DEV = "D1686E961BF708B6355DC015429B91C01DE50A5F24F6370D6E0E62B322857F40";
    public static final String CONSUMER_SECRET_PROD = "D1686E961BF708B6355DC015429B91C01DE50A5F24F6370D6E0E62B322857F40";

    public static String access_token_dev;
    //public static String access_token_qa;
    //public static String access_token_prod;

    public static long prevTimeDev = System.currentTimeMillis();
    //public static long prevTimeQa = System.currentTimeMillis();
    //public static long prevTimeProd = System.currentTimeMillis();

    public static String instance_url_dev = "https://horsepowr--devlatest.my.salesforce.com";
    //public static String instance_url_qa = "https://horsepowr--uat2.my.salesforce.com";
    //public static String instance_url_prod = "https://horsepowr--uat2.my.salesforce.com";

    public static void main(String[] args) {
        AuthUtils apiCalls=new AuthUtils();
        apiCalls.getConnectionDetails();
    }
    
    RegisterModel rModel = null;
    public Auth getConnectionDetail(String org,RegisterModel rModel) {
    	this.rModel = rModel;
    	if ( org.equals("prod") ) {
    		return getProdConnectionDetails();
    	} else if ( org.equals("qa") ) {
    		
    	} 
    	
    	return getDevConnectionDetails();
    }
    
    

    public Auth getConnectionDetails() {
        Auth auth=new Auth();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            //String baseUrl = SF_LOGIN_DEV_URL + "/services/oauth2/token";
            String baseUrl = SF_LOGIN_PROD_URL + "/services/oauth2/token";
            String projectPath = System.getProperty("user.dir");
            String certFileName = projectPath + File.separator + "certserver.der";

            HttpPost oauthPost = new HttpPost(baseUrl);

            List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
            parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
            //auth.setJwtToken(getJWT(CONSUMER_KEY_DEV, USERNAME_DEV, SF_LOGIN_DEV_URL));
            auth.setJwtToken(getJWT(CONSUMER_KEY_PROD, USERNAME_PROD, SF_LOGIN_PROD_URL,certFileName));
            parametersBody.add(new BasicNameValuePair("assertion", auth.getJwtToken()));
            oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, "UTF-8"));

            System.out.println("POST " + baseUrl + "...\n");
            HttpResponse response = client.execute(oauthPost);
            int code = response.getStatusLine().getStatusCode();

            if (code == 200) {

                JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
                access_token_dev = oauthLoginResponse.getString("access_token");
                instance_url_dev = oauthLoginResponse.getString("instance_url");
                auth.setAccessToken(access_token_dev);
                auth.setInstanceUrl(instance_url_dev);
                System.out.println("Dev Token is " + access_token_dev);
                System.out.println("Dev Url is " + instance_url_dev);

            } else {
                System.out.println("Dev Error: Token Response " + response.getStatusLine());
            }

        } catch (Exception e) {
            System.out.println("Dev Error: Not able to call Token service " + e.getMessage());
        }
        return auth;
    }
    
    public Auth getDevConnectionDetails() {
        Auth auth=new Auth();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            //String baseUrl = SF_LOGIN_DEV_URL + "/services/oauth2/token";
            String baseUrl = SF_LOGIN_DEV_URL + "/services/oauth2/token";

            HttpPost oauthPost = new HttpPost(baseUrl);

            List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
            parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
            //auth.setJwtToken(getJWT(CONSUMER_KEY_DEV, USERNAME_DEV, SF_LOGIN_DEV_URL));
            //auth.setJwtToken(getJWT("3MVG9dCCPs.KiE4RHML_5MZBsG3zCnjnQclwubBW_w1JJO5O__Oixi_HFyJWy0C43cD3NAana3u78fHK2m9tG", USERNAME_DEV, SF_LOGIN_DEV_URL));
            auth.setJwtToken(getJWT(rModel.getClientConsumer(), rModel.getUserName(), SF_LOGIN_DEV_URL,rModel.getFilePath() ));
            parametersBody.add(new BasicNameValuePair("assertion", auth.getJwtToken()));
            oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, "UTF-8"));

            System.out.println("POST " + baseUrl + "...\n");
            HttpResponse response = client.execute(oauthPost);
            int code = response.getStatusLine().getStatusCode();

            if (code == 200) {

                JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
                access_token_dev = oauthLoginResponse.getString("access_token");
                instance_url_dev = oauthLoginResponse.getString("instance_url");
                auth.setAccessToken(access_token_dev);
                auth.setInstanceUrl(instance_url_dev);
                System.out.println("Dev Token is " + access_token_dev);
                System.out.println("Dev Url is " + instance_url_dev);

            } else {
                System.out.println("Dev Error: Token Response " + response.getStatusLine());
            }

        } catch (Exception e) {
            System.out.println("Dev Error: Not able to call Token service " + e.getMessage());
        }
        return auth;
    }
    
    public Auth getProdConnectionDetails() {
        Auth auth=new Auth();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            //String baseUrl = SF_LOGIN_DEV_URL + "/services/oauth2/token";
            String baseUrl = SF_LOGIN_PROD_URL + "/services/oauth2/token";
           
            HttpPost oauthPost = new HttpPost(baseUrl);

            List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
            parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
            //auth.setJwtToken(getJWT(CONSUMER_KEY_DEV, USERNAME_DEV, SF_LOGIN_DEV_URL));
            auth.setJwtToken(getJWT(rModel.getClientConsumer(), rModel.getUserName(), SF_LOGIN_PROD_URL,rModel.getFilePath() ));
            parametersBody.add(new BasicNameValuePair("assertion", auth.getJwtToken()));
            oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, "UTF-8"));

            System.out.println("POST " + baseUrl + "...\n");
            HttpResponse response = client.execute(oauthPost);
            int code = response.getStatusLine().getStatusCode();

            if (code == 200) {

                JSONObject oauthLoginResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
                access_token_dev = oauthLoginResponse.getString("access_token");
                instance_url_dev = oauthLoginResponse.getString("instance_url");
                auth.setAccessToken(access_token_dev);
                auth.setInstanceUrl(instance_url_dev);
                System.out.println("Dev Token is " + access_token_dev);
                System.out.println("Dev Url is " + instance_url_dev);

            } else {
                System.out.println("Dev Error: Token Response " + response.getStatusLine());
            }

        } catch (Exception e) {
            System.out.println("Dev Error: Not able to call Token service " + e.getMessage());
        }
        return auth;
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
            //String projectPath = System.getProperty("user.dir");
    		//String certFileName = projectPath + File.separator + "certserver.der";
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
    
    public RSAPrivateKey readPrivateKey(File file) throws Exception {
        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

        String privateKeyPEM = key
          .replace("-----BEGIN RSA PRIVATE KEY-----", "")
          .replaceAll(System.lineSeparator(), "")
          .replace("-----END RSA PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

}
