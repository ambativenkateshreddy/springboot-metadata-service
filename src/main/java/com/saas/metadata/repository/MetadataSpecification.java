package com.saas.metadata.repository;

import com.saas.metadata.entity.MetadataEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

/**
 * Composable JPA Specifications for dynamic metadata queries.
 * Use with JpaSpecificationExecutor.findAll(Specification, Pageable).
 */
public final class MetadataSpecification {

    private MetadataSpecification() {}

    public static Specification<MetadataEntity> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<MetadataEntity> hasEntityType(String entityType) {
        return (root, query, cb) ->
                entityType == null ? cb.conjunction()
                        : cb.equal(root.get("entityType"), entityType);
    }

    public static Specification<MetadataEntity> hasStatus(MetadataEntity.MetadataStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<MetadataEntity> nameLike(String keyword) {
        return (root, query, cb) ->
                keyword == null || keyword.isBlank() ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")),
                                "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<MetadataEntity> createdAfter(Instant from) {
        return (root, query, cb) ->
                from == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<MetadataEntity> createdBefore(Instant to) {
        return (root, query, cb) ->
                to == null ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<MetadataEntity> createdBy(String actor) {
        return (root, query, cb) ->
                actor == null ? cb.conjunction()
                        : cb.equal(root.get("createdBy"), actor);
    }

    /**
     * Convenience builder combining the common filters.
     */
    public static Specification<MetadataEntity> buildFilter(
            String entityType,
            MetadataEntity.MetadataStatus status,
            String nameKeyword,
            Instant from,
            Instant to) {

        return Specification.where(notDeleted())
                .and(hasEntityType(entityType))
                .and(hasStatus(status))
                .and(nameLike(nameKeyword))
                .and(createdAfter(from))
                .and(createdBefore(to));
    }
}
