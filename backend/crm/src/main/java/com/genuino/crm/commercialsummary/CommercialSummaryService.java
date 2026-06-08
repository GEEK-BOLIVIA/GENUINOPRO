package com.genuino.crm.commercialsummary;

import com.genuino.crm.activity.LeadActivity;
import com.genuino.crm.activity.LeadActivityRepository;
import com.genuino.crm.commercialsummary.dto.*;
import com.genuino.crm.inbox.domain.LeadInbox;
import com.genuino.crm.inbox.infra.LeadInboxRepository;
import com.genuino.crm.opportunity.OpportunityTimelineService;
import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.dto.OpportunityTimelineResponse;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import com.genuino.crm.quoting.common.domain.TypedProforma;
import com.genuino.crm.quoting.common.infra.TypedProformaRepository;
import com.genuino.crm.task.CommercialTask;
import com.genuino.crm.task.CommercialTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genuino.crm.audit.domain.AuditEvent;
import com.genuino.crm.audit.infra.AuditEventRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class CommercialSummaryService {

    private final LeadInboxRepository leadInboxRepository;
    private final OpportunityRepository opportunityRepository;
    private final CommercialTaskRepository commercialTaskRepository;
    private final TypedProformaRepository typedProformaRepository;
    private final LeadActivityRepository leadActivityRepository;
    private final OpportunityTimelineService opportunityTimelineService;
    private final AuditEventRepository auditEventRepository;

    public CommercialSummaryService(
            LeadInboxRepository leadInboxRepository,
            OpportunityRepository opportunityRepository,
            CommercialTaskRepository commercialTaskRepository,
            TypedProformaRepository typedProformaRepository,
            LeadActivityRepository leadActivityRepository,
            OpportunityTimelineService opportunityTimelineService,
            AuditEventRepository auditEventRepository
    ) {
        this.leadInboxRepository = leadInboxRepository;
        this.opportunityRepository = opportunityRepository;
        this.commercialTaskRepository = commercialTaskRepository;
        this.typedProformaRepository = typedProformaRepository;
        this.leadActivityRepository = leadActivityRepository;
        this.opportunityTimelineService = opportunityTimelineService;
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional(readOnly = true)
    public CommercialSummaryResponse getByLeadId(String leadId) {
        LeadInbox lead = leadInboxRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado"));
        List<LeadInbox> relatedLeads =
                lead.phone == null || lead.phone.isBlank()
                        ? List.of(lead)
                        : leadInboxRepository.findRelatedByNormalizedPhoneOrderByReceivedAtDesc(lead.phone);

        if (relatedLeads.stream().noneMatch(item -> Objects.equals(item.id, lead.id))) {
            relatedLeads = new ArrayList<>(relatedLeads);
            relatedLeads.add(lead);
        }

        List<CommercialSummaryRelatedLeadResponse> relatedLeadResponses =
                relatedLeads.stream()
                        .map(this::mapRelatedLead)
                        .toList();

        Optional<Opportunity> opportunityOpt = opportunityRepository.findByLeadInboxId(leadId);
        Opportunity opportunity = opportunityOpt.orElse(null);
        String opportunityId = opportunity != null ? opportunity.id : null;

        List<CommercialTask> tasks = collectTasks(leadId, opportunityId);
        List<TypedProforma> proformas = opportunityId == null
                ? List.of()
                : typedProformaRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId);

        List<CommercialSummaryTimelineItemResponse> timeline = new ArrayList<>();
                timeline.add(buildLeadCreatedItem(lead, opportunityId));
                timeline.addAll(mapLeadActivities(leadActivityRepository.findByLeadIdOrderByCreatedAtDesc(leadId), opportunityId));
                timeline.addAll(mapTaskAuditEvents(tasks));
                timeline.addAll(mapProformasToTimeline(proformas));

        if (opportunityId != null) {
            timeline.addAll(mapOpportunityTimeline(opportunityTimelineService.getTimeline(opportunityId), opportunityId));
        }

        timeline = timeline.stream()
                .filter(item -> item.timestamp() != null)
                .sorted(Comparator.comparing(item -> String.valueOf(item.timestamp()), Comparator.reverseOrder()))
                .toList();

        List<CommercialSummaryTaskResponse> taskResponses = tasks.stream()
                .map(this::mapTask)
                .toList();

        List<CommercialSummaryProformaResponse> proformaResponses = proformas.stream()
                .map(this::mapProforma)
                .toList();

                return new CommercialSummaryResponse(
                        mapLead(lead),
                        opportunity != null ? mapOpportunity(opportunity) : null,
                        relatedLeadResponses,
                        timeline,
                        taskResponses,
                        proformaResponses,
                        buildProformaGroups(proformas, tasks, timeline),
                        buildMetrics(tasks, proformas, timeline)
                );
    }

    private List<CommercialTask> collectTasks(String leadId, String opportunityId) {
        Map<UUID, CommercialTask> tasksById = new LinkedHashMap<>();

        commercialTaskRepository.findByLeadIdOrderByDueAtAsc(leadId)
                .forEach(task -> tasksById.put(task.id, task));

        if (opportunityId != null) {
            commercialTaskRepository.findByOpportunityIdOrderByDueAtAsc(opportunityId)
                    .forEach(task -> tasksById.put(task.id, task));
        }

        return tasksById.values().stream()
                .sorted(Comparator.comparing(
                        task -> task.dueAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }

    private CommercialSummaryLeadResponse mapLead(LeadInbox lead) {
        return new CommercialSummaryLeadResponse(
                lead.id,
                lead.fullName,
                lead.phone,
                lead.source,
                lead.channel,
                lead.status,
                lead.assignedSellerId,
                lead.messagePreview,
                lead.receivedAt,
                lead.createdAt
        );
    }

    private CommercialSummaryOpportunityResponse mapOpportunity(Opportunity opportunity) {
        return new CommercialSummaryOpportunityResponse(
                opportunity.id,
                opportunity.customerId,
                opportunity.leadInboxId,
                opportunity.title,
                opportunity.stage,
                opportunity.source,
                opportunity.ownerUserId,
                opportunity.notes,
                opportunity.createdAt,
                opportunity.updatedAt
        );
    }

        private CommercialSummaryTaskResponse mapTask(CommercialTask task) {
        return new CommercialSummaryTaskResponse(
                task.id,
                task.leadId,
                task.opportunityId,
                task.proformaId,
                task.title,
                task.description,
                task.status,
                task.priority,
                task.assignedTo,
                task.dueAt,
                task.createdAt,
                task.completedAt
        );
        }
    private CommercialSummaryProformaResponse mapProforma(TypedProforma proforma) {
        return new CommercialSummaryProformaResponse(
                proforma.getId(),
                proforma.getOpportunityId(),
                proforma.getCustomerId(),
                proforma.getType() != null ? proforma.getType().name() : null,
                proforma.getStatus() != null ? proforma.getStatus().name() : null,
                proforma.getCurrency(),
                proforma.getTotal(),
                proforma.getEstimatedProfit(),
                proforma.getVersion(),
                proforma.getCreatedBy(),
                proforma.getCreatedAt(),
                proforma.getApprovedBy(),
                proforma.getApprovedAt(),
                proforma.getRejectionReason()
        );
    }

    private CommercialSummaryTimelineItemResponse buildLeadCreatedItem(LeadInbox lead, String opportunityId) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("phone", lead.phone);
        metadata.put("fullName", lead.fullName);
        metadata.put("assignedSellerId", lead.assignedSellerId);
        metadata.put("messagePreview", lead.messagePreview);

        return new CommercialSummaryTimelineItemResponse(
                "LEAD_CREATED",
                "LEAD",
                "Contacto creado",
                lead.messagePreview,
                lead.receivedAt != null ? lead.receivedAt : lead.createdAt,
                lead.source,
                lead.assignedSellerId,
                opportunityId,
                null,
                null,
                metadata
        );
    }

    private List<CommercialSummaryTimelineItemResponse> mapLeadActivities(List<LeadActivity> activities, String opportunityId) {
        return activities.stream()
                .map(activity -> new CommercialSummaryTimelineItemResponse(
                        activity.type,
                        "MANUAL_ACTIVITY",
                        titleOrDefault(activity.type, "Seguimiento manual"),
                        activity.description,
                        activity.createdAt,
                        "LEAD_ACTIVITY",
                        activity.createdBy,
                        opportunityId,
                        null,
                        null,
                        metadata("leadId", activity.leadId, "activityId", activity.id)
                ))
                .toList();
    }

    private List<CommercialSummaryTimelineItemResponse> mapProformasToTimeline(List<TypedProforma> proformas) {
        return proformas.stream()
                .map(proforma -> new CommercialSummaryTimelineItemResponse(
                        "PROFORMA_" + safe(proforma.getStatus() != null ? proforma.getStatus().name() : null, "CREATED"),
                        "PROFORMA",
                        "Proforma " + (proforma.getType() != null ? proforma.getType().name() : "") + " " + safe(proforma.getStatus() != null ? proforma.getStatus().name() : null, ""),
                        proforma.getNotes(),
                        proforma.getCreatedAt(),
                        "TYPED_PROFORMA",
                        proforma.getCreatedBy(),
                        proforma.getOpportunityId(),
                        proforma.getId(),
                        null,
                        metadata(
                                "total", proforma.getTotal(),
                                "currency", proforma.getCurrency(),
                                "estimatedProfit", proforma.getEstimatedProfit(),
                                "version", proforma.getVersion()
                        )
                ))
                .toList();
    }

    private List<CommercialSummaryTimelineItemResponse> mapOpportunityTimeline(OpportunityTimelineResponse response, String opportunityId) {
        List<CommercialSummaryTimelineItemResponse> items = new ArrayList<>();

        if (response.getEvents() != null) {
            for (Map<String, Object> event : response.getEvents()) {
                String eventType = String.valueOf(event.getOrDefault("type", "SYSTEM_EVENT"));
                if ("LEAD_CREATED".equals(eventType) || "PROFORMA_CREATED".equals(eventType)) {
                    continue;
                }

                items.add(new CommercialSummaryTimelineItemResponse(
                        eventType,
                        resolveCategory(eventType),
                        eventType,
                        String.valueOf(event.getOrDefault("reason", event.getOrDefault("result", ""))),
                        event.get("timestamp"),
                        "OPPORTUNITY_TIMELINE",
                        event.get("actorUserId") != null ? String.valueOf(event.get("actorUserId")) : null,
                        opportunityId,
                        parseUuid(event.get("proformaId")),
                        null,
                        event
                ));
            }
        }

        if (response.getActivities() != null) {
            response.getActivities().forEach(activity -> items.add(new CommercialSummaryTimelineItemResponse(
                    activity.getType(),
                    "MANUAL_ACTIVITY",
                    activity.getTitle(),
                    activity.getDescription(),
                    activity.getActivityDate(),
                    activity.getSource(),
                    activity.getCreatedBy(),
                    opportunityId,
                    null,
                    null,
                    metadata("activityId", activity.getId())
            )));
        }

        return items;
    }

    private CommercialSummaryMetricsResponse buildMetrics(
            List<CommercialTask> tasks,
            List<TypedProforma> proformas,
            List<CommercialSummaryTimelineItemResponse> timeline
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        long pendingTaskCount = tasks.stream()
                .filter(task -> "PENDING".equalsIgnoreCase(task.status))
                .count();

        long overdueTaskCount = tasks.stream()
                .filter(task -> "PENDING".equalsIgnoreCase(task.status))
                .filter(task -> task.dueAt != null && task.dueAt.isBefore(now))
                .count();

        long approvedProformaCount = proformas.stream()
                .filter(proforma -> proforma.getStatus() != null)
                .filter(proforma -> {
                    String status = proforma.getStatus().name();
                    return "APPROVED".equals(status) || "CLIENT_ACCEPTED".equals(status);
                })
                .count();

        long rejectedProformaCount = proformas.stream()
                .filter(proforma -> proforma.getStatus() != null)
                .filter(proforma -> proforma.getStatus().name().contains("REJECTED"))
                .count();

        BigDecimal totalQuotedAmount = proformas.stream()
                .map(TypedProforma::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal estimatedProfit = proformas.stream()
                .map(TypedProforma::getEstimatedProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Object lastActivityAt = timeline.stream()
                .map(CommercialSummaryTimelineItemResponse::timestamp)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Object nextTaskDueAt = tasks.stream()
                .filter(task -> "PENDING".equalsIgnoreCase(task.status))
                .map(task -> task.dueAt)
                .filter(Objects::nonNull)
                .filter(dueAt -> !dueAt.isBefore(now))
                .min(Comparator.naturalOrder())
                .orElse(null);

        return new CommercialSummaryMetricsResponse(
                tasks.size(),
                pendingTaskCount,
                overdueTaskCount,
                proformas.size(),
                approvedProformaCount,
                rejectedProformaCount,
                totalQuotedAmount,
                estimatedProfit,
                lastActivityAt,
                nextTaskDueAt
        );
    }


    private Map<String, Object> metadata(Object... keyValues) {
        Map<String, Object> metadata = new LinkedHashMap<>();

        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            Object key = keyValues[i];
            Object value = keyValues[i + 1];

            if (key != null) {
                metadata.put(String.valueOf(key), value);
            }
        }

        return metadata;
    }

    private String resolveCategory(String type) {
        if (type == null) return "SYSTEM";
        if (type.startsWith("PROFORMA")) return "PROFORMA";
        if (type.startsWith("OPPORTUNITY")) return "OPPORTUNITY";
        if (type.startsWith("LEAD")) return "LEAD";
        if (type.startsWith("TASK")) return "TASK";
        return "SYSTEM";
    }

    private UUID parseUuid(Object value) {
        if (value == null) return null;
        if (value instanceof UUID uuid) return uuid;
        try {
            return UUID.fromString(String.valueOf(value));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String titleOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private String safe(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

        private List<CommercialSummaryTimelineItemResponse> mapTaskAuditEvents(
                List<CommercialTask> tasks
        ) {
        List<CommercialSummaryTimelineItemResponse> items = new ArrayList<>();

        for (CommercialTask task : tasks) {
                List<AuditEvent> audits = auditEventRepository
                        .findByEntityTypeAndEntityIdOrderByTsAsc(
                                "TASK",
                                task.id.toString()
                        );

                for (AuditEvent audit : audits) {
                String title = switch (audit.action) {
                        case "TASK_CREATED" -> "Tarea creada";
                        case "TASK_COMPLETED" -> "Tarea completada";
                        default -> audit.action;
                };

                items.add(new CommercialSummaryTimelineItemResponse(
                        audit.action,
                        "TASK",
                        title,
                        audit.reason != null ? audit.reason : task.title,
                        audit.ts,
                        "TASK_AUDIT",
                        audit.actorUserId != null ? audit.actorUserId : task.assignedTo,
                        task.opportunityId,
                        task.proformaId,
                        task.id,
                        metadata(
                                "priority", task.priority,
                                "status", task.status,
                                "assignedTo", task.assignedTo,
                                "dueAt", task.dueAt,
                                "createdAt", task.createdAt,
                                "completedAt", task.completedAt
                        )
                ));
                }
        }

        return items;
        }

        private List<CommercialSummaryProformaGroupResponse> buildProformaGroups(
                List<TypedProforma> proformas,
                List<CommercialTask> tasks,
                List<CommercialSummaryTimelineItemResponse> timeline
        ) {
        return proformas.stream()
                .map(proforma -> {
                        UUID proformaId = proforma.getId();

                        List<CommercialSummaryTaskResponse> proformaTasks = tasks.stream()
                                .filter(task -> proformaId.equals(task.proformaId))
                                .map(this::mapTask)
                                .toList();

                        List<CommercialSummaryTimelineItemResponse> proformaTimeline = timeline.stream()
                                .filter(item -> proformaId.equals(item.proformaId()))
                                .toList();

                        return new CommercialSummaryProformaGroupResponse(
                                mapProforma(proforma),
                                proformaTimeline,
                                proformaTasks
                        );
                })
                .toList();
        }

        private List<CommercialTask> collectTasksForLeads(
                List<String> leadIds,
                String opportunityId
        ) {
        Map<UUID, CommercialTask> tasksById = new LinkedHashMap<>();

        for (String relatedLeadId : leadIds) {
                commercialTaskRepository.findByLeadIdOrderByDueAtAsc(relatedLeadId)
                        .forEach(task -> tasksById.put(task.id, task));
        }

        if (opportunityId != null) {
                commercialTaskRepository.findByOpportunityIdOrderByDueAtAsc(opportunityId)
                        .forEach(task -> tasksById.put(task.id, task));
        }

        return tasksById.values().stream()
                .sorted(Comparator.comparing(
                        task -> task.dueAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
        }
private CommercialSummaryRelatedLeadResponse mapRelatedLead(LeadInbox lead) {
    Instant date = lead.receivedAt != null ? lead.receivedAt : lead.createdAt;

    java.time.ZonedDateTime zonedDate = date != null
            ? date.atZone(java.time.ZoneId.of("America/La_Paz"))
            : null;

    Integer year = zonedDate != null ? zonedDate.getYear() : null;
    Integer month = zonedDate != null ? zonedDate.getMonthValue() : null;

    String monthLabel = zonedDate != null
            ? zonedDate.getMonth()
                .getDisplayName(
                    java.time.format.TextStyle.FULL,
                    new java.util.Locale("es", "BO")
                )
            : null;

    return new CommercialSummaryRelatedLeadResponse(
            lead.id,
            lead.fullName,
            lead.phone,
            lead.messagePreview,
            lead.status,
            lead.source,
            lead.channel,
            lead.assignedSellerId,
            lead.receivedAt,
            lead.createdAt,
            year,
            month,
            monthLabel
    );
}
}
