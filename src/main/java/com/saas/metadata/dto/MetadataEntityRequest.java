package com.saas.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MetadataEntityRequest {

    @NotBlank(message = "Entity key is required")
    @Pattern(regexp = "^[a-z0-9_.-]+$", message = "Key must be lowercase alphanumeric with _ . -")
    @Size(max = 128)
    private String entityKey;

    @NotBlank(message = "Name is required")
    @Size(max = 256)
    private String name;

    @Size(max = 2000)
    private String description;

    @Size(max = 64)
    private String category;

    private String metadataJson;

    private String changeSummary;
}
