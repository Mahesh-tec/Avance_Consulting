package com.urlshortener.url_shortener.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UrlController {

    // Internal in-memory map to store shortCode -> UrlMapping
    private final Map<String, UrlMapping> urlMap = new ConcurrentHashMap<>();

    // Inner class to hold original URL and timestamp
    static class UrlMapping {
        private final String originalUrl;
        private final Timestamp createdAt;

        public UrlMapping(String originalUrl) {
            this.originalUrl = originalUrl;
            this.createdAt = new Timestamp(System.currentTimeMillis());
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public Timestamp getCreatedAt() {
            return createdAt;
        }
    }

    // POST endpoint to shorten the URL (under /api)
    @PostMapping("/api/shorten")
    public ResponseEntity<String> shorten(@RequestBody Map<String, String> body) {
        String url = body.get("originalUrl");

        if (url == null || !url.matches("^(http|https)://.*")) {
            return ResponseEntity.badRequest().body("Invalid URL format");
        }

        // Check if already exists and not expired
        for (Map.Entry<String, UrlMapping> entry : urlMap.entrySet()) {
            if (entry.getValue().getOriginalUrl().equals(url) && !isExpired(entry.getValue())) {
                return ResponseEntity.ok("http://localhost:8080/" + entry.getKey());
            }
        }

        // Generate new short code
        String shortCode = UUID.randomUUID().toString().substring(0, 6);
        urlMap.put(shortCode, new UrlMapping(url));
        return ResponseEntity.ok("http://localhost:8080/" + shortCode);
    }

    // GET endpoint to handle redirection using shortCode (direct root mapping, not under /api)
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        UrlMapping mapping = urlMap.get(shortCode);

        if (mapping == null || isExpired(mapping)) {
            urlMap.remove(shortCode);
            response.setContentType("text/html");
            response.getWriter().write("<h1>URL expired or doesn't exist. <a href='/'>Shorten new</a></h1>");
            return;
        }

        response.sendRedirect(mapping.getOriginalUrl());
    }

    // TTL check - 5 minutes
    private boolean isExpired(UrlMapping mapping) {
        long diff = System.currentTimeMillis() - mapping.getCreatedAt().getTime();
        return diff > 5 * 60 * 1000; // 5 minutes
    }
}
