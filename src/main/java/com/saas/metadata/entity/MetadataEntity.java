package com.saas.metadata.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "metadata_entities",
    indexes = {
        @Index(name = "idx_metadata_key", columnList = "meta_key"),
        @Index(name = "idx_metadata_category", columnList = "category"),
        @Index(name = "idx_metadata_status", columnList = "status"),
        @Index(name = "idx_metadata_version", columnList = "version")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "meta_key", nullable = false, unique = true, length = 255)
    private String key;

    @Column(name = "meta_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MetadataStatus status = MetadataStatus.ACTIVE;

    @Column(name = "tags", length = 500)
    private String tags; // comma-separated

    public enum MetadataStatus {
        ACTIVE, INACTIVE, DEPRECATED
    }
}
