package com.cf.files.utility.service;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.cf.files.utility.model.RegisterModel;
import com.cf.files.utility.util.Constants;

public class PropService {
	

	
	public boolean saveData(RegisterModel rModel) {
		
		try {
			
			String orgId = rModel.getOrgId();
			
			Properties prop = new Properties();
			
			 String projectPath = System.getProperty("user.dir");
			 File file = new File(projectPath + File.separator + "project.properties");
			 if ( file.exists() ) {
				 InputStream input = new FileInputStream(file);
				 prop.load(input);
			 }
			
			// set the properties value
	        prop.setProperty(orgId + "." + Constants.LABEL_AWS_ACCESS , rModel.getAwsKey());
	        prop.setProperty(orgId + "." + Constants.LABEL_AWS_SECRET , rModel.getAwsSecret());
	        prop.setProperty(orgId + "." + Constants.LABEL_AWS_BUCKET , rModel.getBucketName());
	        prop.setProperty(orgId + "." + Constants.LABEL_APP_CONSUMER , rModel.getClientConsumer());
	        prop.setProperty(orgId + "." + Constants.LABEL_APP_SECRET , rModel.getClientSecret());
	        prop.setProperty(orgId + "." + Constants.LABEL_SF_USERNAME , rModel.getUserName());
	        
	        if ( rModel.isSandbox() ) {
	        	 prop.setProperty(orgId + "." + Constants.LABEL_SF_SANDBOX , "true");
	        } else {
	        	 prop.setProperty(orgId + "." + Constants.LABEL_SF_SANDBOX , "false");
	        }
	       
	        
	       
			
	        OutputStream output = new FileOutputStream(file);
	        prop.store(output, null);
	        
	        return true;
		} catch (Exception e) {
			System.out.println("Properties could not be saved " + e);
		}
		
		return false;
		
	}
	
	public RegisterModel getData(String orgId) {
		
		try {
			
			RegisterModel rModel = new RegisterModel();
					
			Properties prop = new Properties();
			
			String projectPath = System.getProperty("user.dir");
			File file = new File(projectPath + File.separator + "project.properties");
			
			InputStream input = new FileInputStream(file);
			prop.load(input);
			
			// set the properties value
			rModel.setAwsKey( prop.getProperty(orgId + "." + Constants.LABEL_AWS_ACCESS) );
			rModel.setAwsSecret( prop.getProperty(orgId + "." + Constants.LABEL_AWS_SECRET) );
			rModel.setBucketName( prop.getProperty(orgId + "." + Constants.LABEL_AWS_BUCKET) );
			rModel.setClientConsumer(prop.getProperty(orgId + "." + Constants.LABEL_APP_CONSUMER));
			rModel.setClientSecret(prop.getProperty(orgId + "." + Constants.LABEL_APP_SECRET));
			rModel.setUserName(prop.getProperty(orgId + "." + Constants.LABEL_SF_USERNAME));
			
			String sandbox = prop.getProperty(orgId + "." + Constants.LABEL_SF_SANDBOX);
			if ( "false".equals(sandbox) ) {
				rModel.setSandbox(false);
	        } else {
	        	rModel.setSandbox(true);
	        }
			
			rModel.setOrgId(orgId);
			
			String fileName = orgId + File.separator + "certserver.der";
			rModel.setFilePath(fileName);
	   
			System.out.println("Org Id is " + rModel.getOrgId());
			System.out.println("Org is Sandbox " + rModel.isSandbox());
			System.out.println("AWS key is " + rModel.getAwsKey());
			System.out.println("AWS Secret is " + rModel.getAwsSecret());
			
			return rModel;
	        
	        
		} catch (Exception e) {
			System.out.println("Properties could not be fetched " + e);
		}
		
		return null;
	}
	
	

}
