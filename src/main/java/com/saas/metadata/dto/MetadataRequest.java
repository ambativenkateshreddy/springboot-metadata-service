package com.saas.metadata.dto;

import com.saas.metadata.entity.MetadataEntity.MetadataStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MetadataRequest {

    @NotBlank(message = "Key is required")
    @Size(max = 255, message = "Key must be at most 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._:-]+$", message = "Key may only contain alphanumeric characters, dots, underscores, colons, and hyphens")
    private String key;

    @NotBlank(message = "Value is required")
    private String value;

    @NotBlank(message = "Category is required")
    @Size(max = 100)
    private String category;

    @Size(max = 500)
    private String description;

    private MetadataStatus status = MetadataStatus.ACTIVE;

    @Size(max = 500)
    private String tags;
}
