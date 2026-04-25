package com.saas.metadata;

import com.saas.metadata.audit.AuditService;
import com.saas.metadata.context.TenantContext;
import com.saas.metadata.dto.MetadataRequest;
import com.saas.metadata.dto.MetadataResponse;
import com.saas.metadata.entity.MetadataEntity;
import com.saas.metadata.entity.MetadataEntity.MetadataStatus;
import com.saas.metadata.exception.DuplicateKeyException;
import com.saas.metadata.exception.ResourceNotFoundException;
import com.saas.metadata.repository.MetadataRepository;
import com.saas.metadata.service.MetadataService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetadataServiceTest {

    @Mock MetadataRepository metadataRepository;
    @Mock AuditService auditService;
    @InjectMocks MetadataService metadataService;

    @BeforeEach
    void setup() {
        TenantContext.setCurrentTenant("tenant_acme");
    }

    @AfterEach
    void teardown() {
        TenantContext.clear();
    }

    @Test
    void create_shouldReturnResponse_whenKeyIsNew() {
        MetadataRequest req = new MetadataRequest();
        req.setKey("app.feature.x");
        req.setValue("true");
        req.setCategory("features");
        req.setStatus(MetadataStatus.ACTIVE);

        when(metadataRepository.existsByKey("app.feature.x")).thenReturn(false);
        when(metadataRepository.save(any())).thenAnswer(inv -> {
            MetadataEntity e = inv.getArgument(0);
            e = MetadataEntity.builder()
                    .id(UUID.randomUUID())
                    .key(e.getKey()).value(e.getValue())
                    .category(e.getCategory()).status(e.getStatus()).version(1)
                    .build();
            return e;
        });

        MetadataResponse response = metadataService.create(req);

        assertThat(response).isNotNull();
        assertThat(response.getKey()).isEqualTo("app.feature.x");
        assertThat(response.getVersion()).isEqualTo(1);
        verify(auditService).log(any(), eq("MetadataEntity"), any(), isNull(), anyString());
    }

    @Test
    void create_shouldThrow_whenKeyAlreadyExists() {
        MetadataRequest req = new MetadataRequest();
        req.setKey("duplicate.key");
        req.setValue("v");
        req.setCategory("cat");
        req.setStatus(MetadataStatus.ACTIVE);

        when(metadataRepository.existsByKey("duplicate.key")).thenReturn(true);

        assertThatThrownBy(() -> metadataService.create(req))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("duplicate.key");
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(metadataRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> metadataService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

@Test
void update_shouldIncrementVersion() {
    UUID id = UUID.randomUUID();
    MetadataEntity existing = MetadataEntity.builder()
            .id(id).key("old.key").value("old").category("cat")
            .status(MetadataStatus.ACTIVE).version(1).build();

    MetadataRequest req = new MetadataRequest();
    req.setKey("old.key");  // same key → condition skips existsByKey
    req.setValue("updated");
    req.setCategory("cat");
    req.setStatus(MetadataStatus.ACTIVE);

    when(metadataRepository.findById(id)).thenReturn(Optional.of(existing));
    when(metadataRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    MetadataResponse response = metadataService.update(id, req);

    assertThat(response.getVersion()).isEqualTo(2);
    assertThat(response.getValue()).isEqualTo("updated");
}
}