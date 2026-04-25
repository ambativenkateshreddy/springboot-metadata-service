package com.saas.metadata.repository;

import com.saas.metadata.entity.MetadataEntity;
import com.saas.metadata.entity.MetadataEntity.MetadataStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataRepository extends JpaRepository<MetadataEntity, UUID>,
        JpaSpecificationExecutor<MetadataEntity> {

    Optional<MetadataEntity> findByKey(String key);

    boolean existsByKey(String key);

    Page<MetadataEntity> findByCategory(String category, Pageable pageable);

    Page<MetadataEntity> findByStatus(MetadataStatus status, Pageable pageable);

    @Query("""
        SELECT m FROM MetadataEntity m
        WHERE (:category IS NULL OR m.category = :category)
          AND (:status IS NULL OR m.status = :status)
          AND (:search IS NULL OR LOWER(m.key) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<MetadataEntity> findByFilters(
            @Param("category") String category,
            @Param("status") MetadataStatus status,
            @Param("search") String search,
            Pageable pageable);
}
