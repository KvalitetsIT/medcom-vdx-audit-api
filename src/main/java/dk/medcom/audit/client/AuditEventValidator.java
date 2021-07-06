package dk.medcom.audit.client;

import dk.medcom.audit.client.api.v1.AuditEvent;
import org.apache.commons.lang3.StringUtils;

class AuditEventValidator {
    void validate(AuditEvent<?> auditEvent) {
        if(StringUtils.isBlank(auditEvent.getOrganisationCode())) {
            throw new RuntimeException("org er tom");
        }
    }
}
