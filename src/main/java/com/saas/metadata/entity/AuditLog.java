package com.saas.metadata.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_entity_id", columnList = "entity_id"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private Action action;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public enum Action { CREATE, UPDATE, DELETE, READ }
}
