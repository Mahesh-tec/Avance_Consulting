package com.urlshortener.url_shortener.model;

import java.sql.Timestamp;

public class UrlMapping {
    private String originalUrl;
    private Timestamp createdAt;

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
