package com.aryan.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeminiHelper {

    public static void main(String[] args) {
        String userSummary = "I spend too much on coffee and subscriptions, and I want to save more.";
        String advice = GeminiHelper.getFinancialAdvice(userSummary);
        System.out.println(advice); // This will print the financial advice from Flask
    }

    public static String getFinancialAdvice(String userSummary) {
        try {
            URL url = new URL("http://localhost:5000/financial-advice");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
    
            // Escape the summary properly for JSON
            String escapedSummary = userSummary
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    
            String jsonInput = "{ \"summary\": \"" + escapedSummary + "\" }";
    
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes());
            }
    
            int status = conn.getResponseCode();
            InputStream inputStream = (status < HttpURLConnection.HTTP_BAD_REQUEST)
                    ? conn.getInputStream()
                    : conn.getErrorStream();
    
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
    
            if (status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                return "Error: " + response;
            }
    
            // Extract advice
            String json = response.toString();
            System.out.println(json);
            // int start = json.indexOf("\"advice\":\"");
            // if (start != -1) {
            //     String text = json.substring(start + 9);
            //     int end = text.indexOf("\"");
            //     if (end != -1) {
            //         return text.substring(0, end).replace("\\n", " ").replaceAll("\\s+", " ").trim();
            //     }
            // }
            return json;
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return "Failed to fetch advice from Flask backend.";
    }
}
    
