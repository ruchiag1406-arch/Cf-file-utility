package com.cf.files.utility.controller;

import java.net.InetAddress;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    /**
     * ECS-friendly /hi endpoint
     */
    @GetMapping("/hi")
    public String hi() {
        String hostname = "UNKNOWN";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Hi-v3 from task: " + hostname;
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
