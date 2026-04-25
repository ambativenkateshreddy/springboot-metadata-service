package com.saas.metadata.dto;

import com.saas.metadata.entity.MetadataEntity.MetadataStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class MetadataResponse {
    private UUID id;
    private String key;
    private String value;
    private String category;
    private String description;
    private Integer version;
    private MetadataStatus status;
    private String tags;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
