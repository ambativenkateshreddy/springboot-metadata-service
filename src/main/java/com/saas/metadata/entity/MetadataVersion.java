package com.saas.metadata.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "metadata_versions",
    indexes = @Index(name = "idx_mv_entity_ver", columnList = "metadata_entity_id, version_number"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MetadataVersion extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_entity_id", nullable = false)
    private MetadataEntity metadataEntity;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "snapshot_json", columnDefinition = "JSON", nullable = false)
    private String snapshotJson;

    @Column(name = "change_summary", length = 512)
    private String changeSummary;
}
