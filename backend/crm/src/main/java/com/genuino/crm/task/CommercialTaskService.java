package com.genuino.crm.task;

import com.genuino.crm.audit.AuditService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommercialTaskService {

    private final CommercialTaskRepository repository;
    private final AuditService auditService;

    public CommercialTaskService(
            CommercialTaskRepository repository,
            AuditService auditService
    ) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public List<CommercialTask> getTasksByLead(String leadId) {
        return repository.findByLeadIdOrderByDueAtAsc(leadId);
    }

    public List<CommercialTask> getTasksBySeller(String assignedTo) {
        return repository.findByAssignedToOrderByDueAtAsc(assignedTo);
    }

    public CommercialTask createTask(
            String leadId,
            String title,
            String description,
            String priority,
            String assignedTo,
            OffsetDateTime dueAt
    ) {
        CommercialTask task = new CommercialTask();

        task.leadId = leadId;
        task.title = title;
        task.description = description;
        task.priority = priority;
        task.assignedTo = assignedTo;
        task.dueAt = dueAt;

        return saveAndAuditCreated(task);
    }

    public List<CommercialTask> getAllTasks() {
        return repository.findAll();
    }

    public CommercialTask completeTask(UUID taskId) {
        CommercialTask task = repository.findById(taskId)
                .orElseThrow(() ->
                        new RuntimeException("Tarea no encontrada"));

        task.completedAt = OffsetDateTime.now();             
        task.status = "COMPLETED";

        CommercialTask saved = repository.save(task);

        auditService.log(
                "TASK_COMPLETED",
                "TASK",
                saved.id.toString(),
                null,
                saved,
                saved.title,
                "SUCCESS",
                null
        );

        return saved;
    }

    public List<CommercialTask> getTasksByOpportunity(String opportunityId) {
        return repository.findByOpportunityIdOrderByDueAtAsc(opportunityId);
    }

    public List<CommercialTask> getTasksByProforma(UUID proformaId) {
        return repository.findByProformaIdOrderByDueAtAsc(proformaId);
    }

    public CommercialTask createOpportunityTask(
            String opportunityId,
            String title,
            String description,
            String priority,
            String assignedTo,
            OffsetDateTime dueAt
    ) {
        CommercialTask task = new CommercialTask();

        task.opportunityId = opportunityId;
        task.title = title;
        task.description = description;
        task.priority = priority;
        task.assignedTo = assignedTo;
        task.dueAt = dueAt;

        return saveAndAuditCreated(task);
    }

    public CommercialTask createProformaTask(
            String opportunityId,
            UUID proformaId,
            String title,
            String description,
            String priority,
            String assignedTo,
            OffsetDateTime dueAt
    ) {
        CommercialTask task = new CommercialTask();

        task.opportunityId = opportunityId;
        task.proformaId = proformaId;
        task.title = title;
        task.description = description;
        task.priority = priority;
        task.assignedTo = assignedTo;
        task.dueAt = dueAt;

        return saveAndAuditCreated(task);
    }

    public void createFollowUpPlan(String leadId, String assignedTo) {
        OffsetDateTime now = OffsetDateTime.now();

        createTask(leadId, "Contacto inicial", "Primer contacto comercial", "ALTA", assignedTo, now);
        createTask(leadId, "Seguimiento día 3", "Primer seguimiento comercial", "MEDIA", assignedTo, now.plusDays(3));
        createTask(leadId, "Seguimiento día 5", "Segundo seguimiento comercial", "MEDIA", assignedTo, now.plusDays(5));
        createTask(leadId, "Seguimiento día 7", "Tercer seguimiento comercial", "MEDIA", assignedTo, now.plusDays(7));
    }

    public void createFollowUpPlan(
            String leadId,
            String opportunityId,
            String assignedTo
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        createFollowUpTask(leadId, opportunityId, "Contacto inicial", "Primer contacto comercial", "ALTA", assignedTo, now);
        createFollowUpTask(leadId, opportunityId, "Seguimiento día 3", "Primer seguimiento comercial", "MEDIA", assignedTo, now.plusDays(3));
        createFollowUpTask(leadId, opportunityId, "Seguimiento día 5", "Segundo seguimiento comercial", "MEDIA", assignedTo, now.plusDays(5));
        createFollowUpTask(leadId, opportunityId, "Seguimiento día 7", "Tercer seguimiento comercial", "MEDIA", assignedTo, now.plusDays(7));
    }

    private CommercialTask createFollowUpTask(
            String leadId,
            String opportunityId,
            String title,
            String description,
            String priority,
            String assignedTo,
            OffsetDateTime dueAt
    ) {
        CommercialTask task = new CommercialTask();

        task.leadId = leadId;
        task.opportunityId = opportunityId;
        task.title = title;
        task.description = description;
        task.priority = priority;
        task.assignedTo = assignedTo;
        task.dueAt = dueAt;

        return saveAndAuditCreated(task);
    }

    private CommercialTask saveAndAuditCreated(CommercialTask task) {
        CommercialTask saved = repository.save(task);

        auditService.log(
                "TASK_CREATED",
                "TASK",
                saved.id.toString(),
                null,
                saved,
                saved.title,
                "SUCCESS",
                null
        );

        return saved;
    }
}