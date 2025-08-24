package com.localredditchat.localredditchatbackend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reddit")
public class RedditAuthController {

    @Value("${reddit.client.id}")
    private String clientId;

    @Value("${reddit.client.secret}")
    private String clientSecret;

    @Value("${reddit.redirect.uri}")
    private String redirectUri;

    @Value("${reddit.api.user_agent}")
    private String userAgent;

    @GetMapping("/login")
    public void redditLogin(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString(); // Add proper state validation in prod
        String authUrl = "https://www.reddit.com/api/v1/authorize?" +
                "client_id=" + clientId +
                "&response_type=code" +
                "&state=" + state +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&duration=permanent" +
                "&scope=identity";
        response.sendRedirect(authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> redditCallback(@RequestParam("code") String code, @RequestParam("state") String state) {

        RestTemplate restTemplate = new RestTemplate();

        // Exchange code for access token
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity("https://www.reddit.com/api/v1/access_token", tokenRequest, Map.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to obtain Reddit access token.");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // Get user info using access token
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        userHeaders.set("User-Agent", userAgent);
        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResponse = restTemplate.exchange("https://oauth.reddit.com/api/v1/me", HttpMethod.GET, userRequest, Map.class);

        if (userResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch Reddit user info.");
        }

        String redditUsername = (String) userResponse.getBody().get("name");

        // Here you can create or fetch the User entity in your DB by redditUsername
        // Optionally create a session or JWT token for your app and return that.
        return ResponseEntity.ok("User logged in with Reddit username: " + redditUsername);
    }
}
