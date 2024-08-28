package com.school_system.config;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MongoDBRestarter {

    public static void restartMongoDB() {
        // Define the path to the restart script
        String restartScriptPath = "/usr/home/username/jobs/start_mongodb.sh";

        // Execute the script to restart MongoDB
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", restartScriptPath);
            processBuilder.inheritIO();  // This will display the script's output in the console
            Process process = processBuilder.start();
            process.waitFor();  // Wait for the script to complete

        } catch (IOException | InterruptedException e) {
            log.error("Failed to execute MongoDB restart script.");
            e.printStackTrace();
        }
    }
}