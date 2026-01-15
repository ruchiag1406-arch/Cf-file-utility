package com.cf.files.utility.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController {

    /**
     * ECS-friendly /kill endpoint
     */
    @GetMapping("/kill")
    public String kill() {
        new Thread(() -> {
            try {
                Thread.sleep(1000); // delay to return response
            } catch (InterruptedException ignored) {}
            
            System.exit(1); // kills container → ECS will replace it
        }).start();

        String containerId = System.getenv("HOSTNAME"); // ECS sets this automatically
        return "Instance " + containerId + " is shutting down. ECS will launch a new task.";
    }

}

