package com.saas.metadata.dto;

import com.saas.metadata.entity.MetadataEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.Map;

public final class MetadataDto {

    private MetadataDto() {}

    // ── Request ──────────────────────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        private String name;

        @NotBlank(message = "Entity type is required")
        @Size(max = 100)
        @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "Entity type may only contain letters, digits, underscores, hyphens")
        private String entityType;

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        private String description;

        private Map<@NotBlank String, String> attributes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @Size(max = 255)
        private String name;

        @Size(max = 5000)
        private String description;

        private MetadataEntity.MetadataStatus status;

        private Map<String, String> attributes;

        private boolean bumpVersion = false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilterRequest {
        private String entityType;
        private MetadataEntity.MetadataStatus status;
        private String nameKeyword;
        private Instant createdAfter;
        private Instant createdBefore;
        private String createdBy;
    }

    // ── Response ─────────────────────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String entityType;
        private String description;
        private MetadataEntity.MetadataStatus status;
        private Integer schemaVersion;
        private Map<String, String> attributes;
        private Long version;
        private String createdBy;
        private String updatedBy;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long id;
        private String name;
        private String entityType;
        private MetadataEntity.MetadataStatus status;
        private Integer schemaVersion;
        private Instant updatedAt;
    }
}
