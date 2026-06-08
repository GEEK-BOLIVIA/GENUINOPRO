package com.genuino.crm.task;

import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class CommercialTaskController {

    private final CommercialTaskService service;

    public CommercialTaskController(CommercialTaskService service) {
        this.service = service;
    }

    @GetMapping("/api/leads/{leadId}/tasks")
    public List<CommercialTask> getLeadTasks(@PathVariable String leadId) {
        return service.getTasksByLead(leadId);
    }

    @PostMapping("/api/leads/{leadId}/tasks")
    public CommercialTask createLeadTask(
            @PathVariable String leadId,
            @RequestBody Map<String, String> body
    ) {
        return service.createTask(
                leadId,
                body.getOrDefault("title", "Nueva tarea"),
                body.getOrDefault("description", ""),
                body.getOrDefault("priority", "MEDIA"),
                body.getOrDefault("assignedTo", "admin"),
                body.get("dueAt") != null && !body.get("dueAt").isBlank()
                        ? OffsetDateTime.parse(body.get("dueAt"))
                        : null
        );
    }

    @GetMapping("/api/opportunities/{opportunityId}/tasks")
    public List<CommercialTask> getOpportunityTasks(
            @PathVariable String opportunityId
    ) {
        return service.getTasksByOpportunity(
                opportunityId
        );
    }

    @GetMapping("/api/tasks")
    public List<CommercialTask> getSellerTasks(
            @RequestParam(defaultValue = "admin") String assignedTo
    ) {
        return service.getTasksBySeller(assignedTo);
    }

    @GetMapping("/api/tasks/all")
    public List<CommercialTask> getAllTasks() {
        return service.getAllTasks();
    }

    @PatchMapping("/api/tasks/{taskId}/complete")
    public CommercialTask completeTask(
            @PathVariable UUID taskId
    ) {
        return service.completeTask(taskId);
    }
}