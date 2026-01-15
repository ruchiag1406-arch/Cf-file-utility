package com.cf.files.utility.controller;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cf.files.utility.amazon.service.AmazonService;
import com.cf.files.utility.model.AwsUserModel;
import com.cf.files.utility.model.RegisterModel;
import com.cf.files.utility.model.ResponseModel;
import com.cf.files.utility.service.FileService;
import com.cf.files.utility.service.PropService;
import com.cf.files.utility.service.SfService;

@RestController
public class AdminController {
	
	@PostMapping(path = "/register")
	public ResponseModel registerClient(@RequestParam(required = false) String orgId,
			@RequestParam(required = false) String orgName,
			@RequestParam(required = false) String bucketName,
			@RequestParam(required = false) String clientId,
			@RequestParam(required = false) String clientSecret,
			@RequestParam(required = false) String userName,
			@RequestParam(required = false) boolean isSandbox,
			@RequestParam(required = false) String nameSpace,
			@RequestParam(required = false) MultipartFile file) {
		
		System.out.println("isSandbox " + isSandbox);
		System.out.println("nameSpace " + nameSpace);
		//System.exit(0);
		ResponseModel resModel = new ResponseModel();
		String errorMsg = null;
		String filePath = null;
		
		//save certificate file
		System.out.println("Org Id is " + orgId);
		filePath = new FileService().save(file, orgId);
		RegisterModel rModel = new RegisterModel(orgId, orgName, isSandbox, bucketName, clientId, clientSecret, userName, filePath, nameSpace);
		
		AmazonService aService = new AmazonService();
		AwsUserModel userModel= null;
		
		// create user
		if ( filePath!=null ) {
			String awsUname = orgName;// + "-user";
			//rModel.setUserName(awsUname);
			userModel=aService.createUser(awsUname);
		} else {
			System.out.println("System not able to save file");
			errorMsg = "System not able to save file";
		}
		
		//create access keys if user is present
		if ( userModel!=null && errorMsg==null ) {
			userModel = aService.createAccessKey(userModel);
		} else {
			System.out.println("System not able to create user");
			errorMsg = "System not able to create user";
		}
		

		if ( userModel!=null && errorMsg==null  ) {
			
			//bucket creation delayed
			userModel.setBucketName(bucketName);
			
			rModel.setAwsKey(userModel.getAccessKeyId());
			rModel.setAwsSecret(userModel.getAccessSecret());
			rModel.setBucketName(userModel.getBucketName());
			resModel.setData(rModel);
			
		} else {
			System.out.println("System not able to access keys");
			errorMsg = "System not able to access keys";
		}
		
		if ( rModel.getAwsKey() !=null && errorMsg==null  ) {
			
			final RegisterModel rModel2 = rModel;
			final AwsUserModel userModel2 = userModel;
			
			new Timer().schedule(
				    new TimerTask() {
				        @Override
				        public void run() {
				            System.out.println("Running after 10 secs");
				            
				            aService.createBucket(userModel2);
							new SfService().saveRecord(rModel2);
							new PropService().saveData(rModel2);
				        }
				    }, 
				    10000
				);
			
			
			
			
			 
		} else {
			System.out.println("System not able to save data");
			errorMsg = "System not able to save data";
		}
		
		
		
		if ( errorMsg!=null ) {
			resModel.setStatus("ERROR");
			resModel.setErrorMsg(errorMsg);
			resModel.setErrorCode("1");
			resModel.setData(errorMsg);
			
		} 
		System.out.println("Response is " + resModel);
		return resModel;
		
	}
	
	@GetMapping("/.well-known/pki-validation/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        
		// Load file as Resource
		Resource resource = new ClassPathResource(fileName);
		

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
