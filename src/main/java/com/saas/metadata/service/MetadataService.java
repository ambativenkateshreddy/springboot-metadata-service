package com.saas.metadata.service;

import com.saas.metadata.audit.AuditService;
import com.saas.metadata.dto.MetadataRequest;
import com.saas.metadata.dto.MetadataResponse;
import com.saas.metadata.dto.PagedResponse;
import com.saas.metadata.entity.AuditLog.Action;
import com.saas.metadata.entity.MetadataEntity;
import com.saas.metadata.entity.MetadataEntity.MetadataStatus;
import com.saas.metadata.exception.DuplicateKeyException;
import com.saas.metadata.exception.ResourceNotFoundException;
import com.saas.metadata.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MetadataService {

    private final MetadataRepository metadataRepository;
    private final AuditService auditService;

    public PagedResponse<MetadataResponse> findAll(String category, MetadataStatus status,
                                                    String search, Pageable pageable) {
        Page<MetadataEntity> page = metadataRepository.findByFilters(category, status, search, pageable);
        return PagedResponse.from(page.map(this::toResponse));
    }

    public MetadataResponse findById(UUID id) {
        MetadataEntity entity = getOrThrow(id);
        auditService.log(id, "MetadataEntity", Action.READ, null, null);
        return toResponse(entity);
    }

    public MetadataResponse findByKey(String key) {
        MetadataEntity entity = metadataRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Metadata not found for key: " + key));
        return toResponse(entity);
    }

    @Transactional
    public MetadataResponse create(MetadataRequest request) {
        if (metadataRepository.existsByKey(request.getKey())) {
            throw new DuplicateKeyException("Metadata key already exists: " + request.getKey());
        }
        MetadataEntity entity = MetadataEntity.builder()
                .key(request.getKey())
                .value(request.getValue())
                .category(request.getCategory())
                .description(request.getDescription())
                .status(request.getStatus())
                .tags(request.getTags())
                .version(1)
                .build();
        MetadataEntity saved = metadataRepository.save(entity);
        auditService.log(saved.getId(), "MetadataEntity", Action.CREATE, null, saved.getValue());
        log.info("Created metadata entity key={} id={}", saved.getKey(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public MetadataResponse update(UUID id, MetadataRequest request) {
        MetadataEntity entity = getOrThrow(id);

        // Key uniqueness check (allow same entity to keep its key)
        if (!entity.getKey().equals(request.getKey()) && metadataRepository.existsByKey(request.getKey())) {
            throw new DuplicateKeyException("Metadata key already exists: " + request.getKey());
        }

        String oldValue = entity.getValue();
        entity.setKey(request.getKey());
        entity.setValue(request.getValue());
        entity.setCategory(request.getCategory());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setTags(request.getTags());
        entity.setVersion(entity.getVersion() + 1);

        MetadataEntity saved = metadataRepository.save(entity);
        auditService.log(saved.getId(), "MetadataEntity", Action.UPDATE, oldValue, saved.getValue());
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        MetadataEntity entity = getOrThrow(id);
        metadataRepository.delete(entity);
        auditService.log(id, "MetadataEntity", Action.DELETE, entity.getValue(), null);
        log.info("Deleted metadata entity id={}", id);
    }

    private MetadataEntity getOrThrow(UUID id) {
        return metadataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Metadata not found: " + id));
    }

    private MetadataResponse toResponse(MetadataEntity e) {
    MetadataResponse r = new MetadataResponse();
    
    r.setId(e.getId());  
    
    r.setKey(e.getKey());
    r.setValue(e.getValue());
    r.setCategory(e.getCategory());
    r.setDescription(e.getDescription());
    r.setVersion(e.getVersion());
    r.setStatus(e.getStatus());
    r.setTags(e.getTags());
    r.setCreatedAt(e.getCreatedAt());
    r.setUpdatedAt(e.getUpdatedAt());
    r.setCreatedBy(e.getCreatedBy());
    r.setUpdatedBy(e.getUpdatedBy());
    
    return r;
}
}
