package com.saas.metadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
public class MetadataManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetadataManagerApplication.class, args);
    }
}
