package com.saas.metadata.repository;

import com.saas.metadata.entity.MetadataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetadataEntityRepository
        extends JpaRepository<MetadataEntity, Long>, JpaSpecificationExecutor<MetadataEntity> {

Optional<MetadataEntity> findByKey(String key);
boolean existsByKey(String key);
    Page<MetadataEntity> findByCategory(String category, Pageable pageable);

    @Query("""
SELECT m FROM MetadataEntity m
WHERE (:category IS NULL OR m.category = :category)
  AND (:search IS NULL OR 
       LOWER(m.key) LIKE LOWER(CONCAT('%', :search, '%')) OR
       LOWER(m.value) LIKE LOWER(CONCAT('%', :search, '%')))
""")
Page<MetadataEntity> search(@Param("category") String category,
                            @Param("search") String search,
                            Pageable pageable);
}
