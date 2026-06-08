package com.genuino.crm.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genuino.crm.audit.domain.AuditEvent;
import com.genuino.crm.audit.infra.AuditEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository repo;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    public AuditService(AuditEventRepository repo, ObjectMapper objectMapper, HttpServletRequest request) {
        this.repo = repo;
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public void log(String action, String entityType, String entityId, Object before, Object after, String reason, String result, String errorCode) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            AuditEvent e = new AuditEvent();
            e.auditId = UUID.randomUUID();
            e.ts = Instant.now();
            e.actorUserId = auth != null ? auth.getName() : null;
            e.actorRole = auth != null ? auth.getAuthorities().toString() : null;
            e.ip = request.getRemoteAddr();
            e.userAgent = request.getHeader("User-Agent");
            e.traceId = request.getHeader("X-Trace-Id");
            e.requestId = request.getHeader("X-Request-Id");
            e.action = action;
            e.entityType = entityType;
            e.entityId = entityId;
            e.beforeJson = before != null ? objectMapper.writeValueAsString(before) : null;
            e.afterJson = after != null ? objectMapper.writeValueAsString(after) : null;
            e.reason = reason;
            e.result = result;
            e.errorCode = errorCode;

            repo.save(e);
        } catch (Exception ignored) {
        }
    }
}