package com.urlshortener.url_shortener.service;

import com.urlshortener.url_shortener.model.UrlMapping;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlService {

    private final Map<String, UrlMapping> urlMap = new ConcurrentHashMap<>();

    public String createShortUrl(String originalUrl) {
        // Return existing short code if not expired
        for (Map.Entry<String, UrlMapping> entry : urlMap.entrySet()) {
            if (entry.getValue().getOriginalUrl().equals(originalUrl) && !isExpired(entry.getValue())) {
                return entry.getKey();
            }
        }

        // Otherwise, generate a new one
        String shortCode = UUID.randomUUID().toString().substring(0, 6);
        urlMap.put(shortCode, new UrlMapping(originalUrl));
        return shortCode;
    }

    public Optional<String> getOriginalUrl(String shortCode) {
        UrlMapping mapping = urlMap.get(shortCode);
        if (mapping == null || isExpired(mapping)) {
            urlMap.remove(shortCode);
            return Optional.empty();
        }
        return Optional.of(mapping.getOriginalUrl());
    }

    private boolean isExpired(UrlMapping mapping) {
        long diff = System.currentTimeMillis() - mapping.getCreatedAt().getTime();
        return diff > 5 * 60 * 1000; // 5 minutes
    }
}
