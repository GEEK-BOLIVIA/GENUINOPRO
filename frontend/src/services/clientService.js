import { apiFetch } from './api';

export async function clientExistsByLeadId(leadId) {
  return apiFetch(`/clients/exists/${leadId}`);
}

export async function createClientAccount(payload) {
  return apiFetch('/clients', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function markLeadAsClient(leadId) {
  return apiFetch(`/leads/${leadId}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status: 'WON' }),
  });
}

export async function markOpportunityAsClient(opportunityId) {
  return apiFetch(`/opportunities/${opportunityId}/stage`, {
    method: 'PATCH',
    body: JSON.stringify({
      stage: 'CLIENTE',
      reason: 'Cliente creado desde proforma aprobada',
    }),
  });
}