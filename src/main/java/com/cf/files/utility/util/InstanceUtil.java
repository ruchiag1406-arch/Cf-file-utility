package com.cf.files.utility.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InstanceUtil {

    private static String cachedInstanceId;

    public static String getInstanceId() {
        if (cachedInstanceId != null) {
            return cachedInstanceId;
        }

        try {
            // IMDSv2 token
            URL tokenUrl = new URL("http://169.254.169.254/latest/api/token");
            HttpURLConnection tokenConn = (HttpURLConnection) tokenUrl.openConnection();
            tokenConn.setRequestMethod("PUT");
            tokenConn.setRequestProperty("X-aws-ec2-metadata-token-ttl-seconds", "21600");

            String token;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(tokenConn.getInputStream()))) {
                token = br.readLine();
            }

            // Instance ID
            URL idUrl = new URL("http://169.254.169.254/latest/meta-data/instance-id");
            HttpURLConnection idConn = (HttpURLConnection) idUrl.openConnection();
            idConn.setRequestProperty("X-aws-ec2-metadata-token", token);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(idConn.getInputStream()))) {
                cachedInstanceId = br.readLine();
            }

        } catch (Exception e) {
            cachedInstanceId = "UNKNOWN";
        }

        return cachedInstanceId;
    }
}
