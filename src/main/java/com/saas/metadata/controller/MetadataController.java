package com.saas.metadata.controller;

import com.saas.metadata.context.TenantContext;
import com.saas.metadata.dto.MetadataRequest;
import com.saas.metadata.dto.MetadataResponse;
import com.saas.metadata.dto.PagedResponse;
import com.saas.metadata.entity.MetadataEntity.MetadataStatus;
import com.saas.metadata.service.MetadataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata")
@RequiredArgsConstructor
public class MetadataController {

    private final MetadataService metadataService;

    /**
     * GET /api/v1/metadata
     * Paginated, filterable list with optional search
     */
    @GetMapping
    public ResponseEntity<PagedResponse<MetadataResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) MetadataStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
        return ResponseEntity.ok(metadataService.findAll(category, status, search, pageable));
    }

    /**
     * GET /api/v1/metadata/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetadataResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(metadataService.findById(id));
    }

    /**
     * GET /api/v1/metadata/key/{key}
     */
    @GetMapping("/key/{key}")
    public ResponseEntity<MetadataResponse> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(metadataService.findByKey(key));
    }

    /**
     * POST /api/v1/metadata
     */
    @PostMapping
    public ResponseEntity<MetadataResponse> create(@Valid @RequestBody MetadataRequest request) {
        MetadataResponse created = metadataService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/v1/metadata/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetadataResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody MetadataRequest request) {
        return ResponseEntity.ok(metadataService.update(id, request));
    }

    /**
     * DELETE /api/v1/metadata/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        metadataService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/metadata/tenant/info — returns current tenant context (useful for debugging)
     */
    @GetMapping("/tenant/info")
    public ResponseEntity<?> tenantInfo() {
        return ResponseEntity.ok(java.util.Map.of("tenant", TenantContext.getCurrentTenant()));
    }
}
