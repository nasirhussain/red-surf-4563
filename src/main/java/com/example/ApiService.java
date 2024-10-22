package com.example;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {

    private final RestTemplate restTemplate;

    public ApiService() {
        this.restTemplate = new RestTemplate();
    }

    // Schedule the method to run every 5 minutes (300,000 ms)
    @Scheduled(fixedRate = 300000)
    public void makeApiCall() {
        String url = "https://red-surf-4563.onrender.com/"; // Replace with your API URL
        try {
            // Make GET request and capture response as String
            String response = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Error occurred while making API call: " + e.getMessage());
        }
    }
}

