package com.saas.metadata.audit;

import com.saas.metadata.context.TenantContext;
import com.saas.metadata.entity.AuditLog;
import com.saas.metadata.entity.AuditLog.Action;
import com.saas.metadata.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID entityId, String entityType, Action action,
                    String oldValue, String newValue) {
        try {
           AuditLog entry = AuditLog.builder()
        .entityId(entityId)
        .entityType(entityType)
        .action(action)
        .oldValue(oldValue)
        .newValue(newValue)
         .performedBy(resolveActor())
        .createdAt(Instant.now())   // 🔥 FIX HERE
        .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to persist audit log for entity {} action {}: {}",
                    entityId, action, e.getMessage());
        }
    }

    private String resolveActor() {
        String tenant = TenantContext.getCurrentTenant();
        return tenant != null ? "system@" + tenant : "system";
    }
}
