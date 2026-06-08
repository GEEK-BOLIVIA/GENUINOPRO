import { apiFetch } from '../api/apiClient';

export async function getCommercialSummary(leadId) {
  return apiFetch(`/leads/${leadId}/commercial-summary`);
}
