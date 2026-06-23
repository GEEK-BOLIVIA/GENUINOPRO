import { apiFetch } from '../api/apiClient';

export async function getLeads() {
  try {
    return await apiFetch('/leads');
  } catch (error) {
    console.error('Error loading leads', error);
    return [];
  }
}

export async function getLeadById(id) {
  return apiFetch(`/leads/${id}`);
}

export async function createLead(payload) {
  return apiFetch('/leads', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function updateLeadStatus(leadId, status) {
  return apiFetch(`/leads/${leadId}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
  });
}

