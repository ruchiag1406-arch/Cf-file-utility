package com.cf.files.utility.controller;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cf.files.utility.model.RegisterModel;
import com.cf.files.utility.service.PropService;
import com.cf.files.utility.util.InstanceUtil;

@RestController
public class TestController {
	
	  /**
     * ECS-friendly /hi endpoint
     */
    public String hi() {
        String hostname = "UNKNOWN";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Hi-v1 from task: " + hostname;
    }

	
	@GetMapping("/load")
	public String load() {
	    long sum = 0;
	    for (long i = 0; i < 1_000_000_000L; i++) {
	        sum += i;
	    }
	    return "DONE";
	}


}