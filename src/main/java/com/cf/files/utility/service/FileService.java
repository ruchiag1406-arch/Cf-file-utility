package com.cf.files.utility.service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class FileService {
	
	
	private final String projectPath = System.getProperty("user.dir");
	private final Path root = Paths.get(projectPath);
	
	 public String save(MultipartFile file, String orgId) {
		 
	    try {
	    	//File certFile = new File(projectPath + File.separator + orgId);
	    	/* if ( certFile.exists() ) {
	    		certFile.delete();
	    	} else {
	    		certFile.createNewFile();
	    	}*/
			String fileName = orgId + File.separator + "certserver.der";
			File folder = new File(projectPath +File.separator + orgId);
			folder.mkdir();
	    	Path target = this.root.resolve(fileName);
	    	System.out.println("File is "  + file.getSize());
	    	Base64.Decoder dec1 = Base64.getDecoder();
	    	InputStream is1 = dec1.wrap(file.getInputStream());
	    	try {
	    		Files.copy(is1,target );
	    		//Files.copy(file.getInputStream(),target );
	    	} catch (Exception e) {
	    		if ( e.getMessage().contains("java.io.IOException: Illegal base64 character") ) {
	    			Files.copy(file.getInputStream(),target );
	    		} else {
	    			System.out.println("Exception while saving file is " + e.getMessage());
	    		}
	    	}
	    	
	    	return target.toString();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Exception while saving certificate file " + e.getMessage());
	    }
	    
	    return null;
	   
	 }
	
}
