package com.flowerable.spring.infra.gemini;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    private Api api;
    private Models models;
    private Config config;

    @Getter @Setter
    public static class Api {
        private String key;
    }

    @Getter @Setter
    public static class Models {
        private String text;
        private String image;
    }

    @Getter @Setter
    public static class Config {
        private double temperature;
        private int maxOutputTokens;
    }
}