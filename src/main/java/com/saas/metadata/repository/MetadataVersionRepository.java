package com.saas.metadata.repository;
import java.util.UUID;
import com.saas.metadata.entity.MetadataVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetadataVersionRepository extends JpaRepository<MetadataVersion, Long> {

    Page<MetadataVersion> findByMetadataEntityId(Long entityId, Pageable pageable);

Optional<MetadataVersion> findByMetadataEntityIdAndVersionNumber(UUID id, int version);
    @Query("SELECT MAX(v.versionNumber) FROM MetadataVersion v WHERE v.metadataEntity.id = :entityId")
    Optional<Integer> findMaxVersionNumber(@Param("entityId") Long entityId);
}
