package com.cf.files.utility.slackwebhook;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.cf.files.utility.model.Auth;
import com.cf.files.utility.model.RegisterModel;
import com.cf.files.utility.service.PropService;
import com.cf.files.utility.util.AuthUtils;



@Controller
public class SlackWebhook {
	
Auth auth;
	
	@PostMapping("/slackWebhook/*")
	public void slackWebhook(HttpServletRequest request , HttpServletResponse  response) throws Exception {
		System.out.println("invokee>>> slackWebhook method"); 
		
		String requestData = request.getReader().lines().collect(Collectors.joining());
		System.out.println("requestData  :" + requestData);
		
		JSONObject myjson = new JSONObject(requestData);
		String Challenge = myjson.optString("challenge");
		
		if(Challenge != null && !Challenge.isEmpty()) {
			System.out.println("challenge is >>" + Challenge);
			response.getWriter().write(Challenge);
		}
		else {
			String token1  = myjson.optString("token");
			System.out.println("Token is " + token1);
			
			JSONObject evtObject  = myjson.optJSONObject("event");
			String  evtType = evtObject.optString("type");
			String evtChannel = evtObject.optString("channel");
			String evtThread_ts = evtObject.optString("thread_ts");
					
			if(evtObject != null && evtType.equals("message")) {
				String message = evtObject.optString("text");
				if(message.equals("") && message == null) {
					System.out.println("text message is null");
				}
				else {
					System.out.println("message is not null");
					
					AuthUtils conn = new AuthUtils();
					RegisterModel rModel = new PropService().getData("00D1k000000ET2O");
					String org = "dev";
					if ( !rModel.isSandbox() ) {
						org = "prod";
					}
					auth = conn.getConnectionDetail(org, rModel);
					//auth = conn.getConnectionDetails("qa");
					
					System.out.println("build connection is succesfull in qa " + auth);
				
					
					//String searchString = "17119" ; //"{'"+evtChannel+"' AND '"+evtThread_ts+"'}"
					//String searchString = evtChannel + "-" + evtThread_ts; 
					String searchString = evtChannel + "+" + evtThread_ts;
					String queryRequestString = "?q="+searchString+"&sobject=RFI__c&sobject=Submittals__c&fields=id,name&defaultLimit=1";
					URL searchUrl = new URL(this.auth.getInstanceUrl() + "/services/data/v55.0/parameterizedSearch/"+queryRequestString);
					System.out.println("url is >>" + searchUrl.getQuery());
					HttpURLConnection searchConnection = (HttpURLConnection)searchUrl.openConnection();
					System.out.println("open connection");    
			        searchConnection.setRequestProperty("Authorization", "Bearer " + this.auth.getAccessToken());
					       
	   		        searchConnection.setRequestProperty("Content-Type", "application/json");
			        searchConnection.setRequestMethod("GET");
			        System.out.println("set request method and request property");     
			        BufferedReader in = new BufferedReader(new InputStreamReader(searchConnection.getInputStream()));
	     	        String output;   
	   	            StringBuffer res = new StringBuffer();
			        while ((output = in.readLine()) != null ) {
	       	           	res.append(output);
			        }
			        in.close();
			        System.out.println("res object >>" + res);
				        
			        JSONObject resObj = new JSONObject(String.valueOf(res));
			        if(resObj != null) {
						JSONArray searchArray  = resObj.optJSONArray("searchRecords");
						if(searchArray != null) {
				        	String recId = null;
				        	String recName = null;
							for(int o = 0 ; o < searchArray.length() ; o++) {
								JSONObject rec  = searchArray.getJSONObject(o);
								if(rec != null) {
									recId = rec.getString("Id");
									recName = rec.getString("Name");
									break;
								}
							}
							if(recId != null) {
								URL feedCreateUrl = new URL(this.auth.getInstanceUrl() + "/services/data/v55.0/sobjects/FeedItem/");
					            JSONObject chatterFeed = new JSONObject();
					            chatterFeed.put("ParentId", recId);
					            chatterFeed.put("Body", message);
								String recBody = chatterFeed.toString(1) ;
								HttpURLConnection feedCreateConnection = (HttpURLConnection)feedCreateUrl.openConnection();
								feedCreateConnection.setRequestMethod("POST");
								feedCreateConnection.setRequestProperty("Authorization", "Bearer " + this.auth.getAccessToken());							       
								feedCreateConnection.setRequestProperty("Content-Type", "application/json");
								feedCreateConnection.setRequestProperty("Accept", "application/json");
								feedCreateConnection.setDoOutput(true);
								try(OutputStream os = feedCreateConnection.getOutputStream()) {
								    byte[] input = recBody.getBytes();
								    os.write(input, 0, input.length);			
								}
						        BufferedReader inRec = new BufferedReader(new InputStreamReader(feedCreateConnection.getInputStream()));
				     	        String reddddd;
				   	            StringBuffer resRec = new StringBuffer();
						        while ((reddddd = inRec.readLine()) != null ) {
						        	resRec.append(reddddd);
						        }
						        inRec.close();
						        System.out.println("resRec >>" + resRec);
							}
						}
			        } 
				}
			}
		}
	}
}
