package com.example.module.springboottest.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientTest {

    public void fetchAndProcess() {
        String apiUrl = "http://localhost:8080/api/public/people/all/0";
        // Replace with your actual API endpoint

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String jsonBody = response.body();
                    processPersonContent(jsonBody);
                } else {
                    System.err.println("Request failed with status code: " + response.statusCode());
                }

            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void processPersonContent(String json) {
        System.out.println("Response: " + json);
        // Regex pattern to capture individual person JSON objects inside the "content" array
        Pattern personPattern = Pattern.compile("\\{\\s*\"id\":\\s*(\\d+),\\s*\"name\":\\s*\"([^\"]+)\",\\s*\"dob\":\\s*\"([^\"]+)\"\\s*}");
        Matcher matcher = personPattern.matcher(json);

        System.out.println("Processing persons list:\n");
        int totalPages = extractIntField(json, "totalPages");
        int totalElements = extractIntField(json, "totalElements");
        boolean first = extractBooleanField(json, "first");
        System.out.println(totalPages + " " + totalElements + " " + first);

        int count = 0;
        while (matcher.find()) {
            String id = matcher.group(1);
            String name = matcher.group(2);
            String dob = matcher.group(3);

            // --- Your custom processing logic for each person ---
            count++;
            System.out.printf("[%d] ID: %s | Name: %s | DOB: %s%n", count, id, name, dob);
        }

        System.out.println("\nSuccessfully processed " + count + " records.");
    }

    private int extractIntField(String json, String fieldName) {
        // Creates a pattern like: "totalPages"\s*:\s*(\d+)
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0; // Default fallback if not found
    }

    private double extractDoubleField(String json, String fieldName) {
        // Looks for: "fieldName" : numbers, optional decimal point, and more numbers
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0.0; // Default fallback if not found
    }

    private boolean extractBooleanField(String json, String fieldName) {
        // Looks for: "fieldName" : true or false (case-insensitive)
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return false; // Default fallback if not found
    }
}
