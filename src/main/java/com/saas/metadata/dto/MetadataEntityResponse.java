package com.saas.metadata.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class MetadataEntityResponse {
    private Long id;
    private String entityKey;
    private String name;
    private String description;
    private String category;
    private Integer schemaVersion;
    private String metadataJson;
    private String tenantId;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
