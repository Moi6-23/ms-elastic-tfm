package com.parking.parkingapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "simulator")
public record AdminAccessProperties(
        String allowedEmail,
        String secretHeaderName,
        String secretHeaderValue
) {}
