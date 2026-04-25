package com.saas.metadata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Represents a registered tenant (organisation).
 * Stored in the master schema. Each tenant owns a dedicated MySQL schema
 * identified by schemaName.
 */
@Entity
@Table(name = "tenants")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "schema_name", nullable = false, unique = true, length = 64)
    private String schemaName;

    @Column(name = "plan", length = 32)
    @Enumerated(EnumType.STRING)
    private Plan plan;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum Plan { FREE, STARTER, PROFESSIONAL, ENTERPRISE }
}
