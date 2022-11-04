package dk.kvalitetsit.audit.client;

import dk.kvalitetsit.audit.client.api.v1.AuditEvent;
import org.apache.commons.lang3.StringUtils;

class AuditEventValidator {
    void validate(AuditEvent<?> auditEvent) {
        if(StringUtils.isBlank(auditEvent.getOrganisationCode())) {
            throw new AuditValidationException("organisationCode is required.");
        }
    }
}
