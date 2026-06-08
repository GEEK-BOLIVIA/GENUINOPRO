import { apiFetch } from '../api/apiClient';

export async function getLeadActivities(leadId) {
  return apiFetch(`/leads/${leadId}/activities`);
}

export async function createLeadActivity(leadId, payload) {
  return apiFetch(`/leads/${leadId}/activities`, {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function getLeadCommercialTimeline(leadId) {
  return apiFetch(`/leads/${leadId}/commercial-timeline`);
}