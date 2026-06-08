import { apiFetch } from '../api/apiClient';

export async function getLeadTasks(leadId) {
  return apiFetch(`/leads/${leadId}/tasks`);
}

export async function createLeadTask(leadId, payload) {
  return apiFetch(`/leads/${leadId}/tasks`, {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function getSellerTasks(assignedTo = 'admin') {
  return apiFetch(`/tasks?assignedTo=${assignedTo}`);
}

export async function getAllTasks() {
  return apiFetch('/tasks/all');
}

export async function getOpportunityTasks(
  opportunityId
) {
  return apiFetch(
    `/opportunities/${opportunityId}/tasks`
  );
}

export async function completeTask(taskId) {
  return apiFetch(`/tasks/${taskId}/complete`, {
    method: 'PATCH',
  });
}