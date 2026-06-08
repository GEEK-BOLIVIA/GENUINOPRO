import { apiFetch } from '../api/apiClient';

export async function getOpportunities() {
  return apiFetch('/opportunities');
}

export async function getOpportunity(id) {
  return apiFetch(`/opportunities/${id}`);
}

export async function getOpportunityTimeline(id) {
  return apiFetch(`/opportunities/${id}/timeline`);
}

export async function getOpportunityDashboard(id) {
  return apiFetch(`/opportunities/${id}/dashboard`);
}

export async function getOpportunityTypedProformas(id) {
  return apiFetch(`/opportunities/${id}/typed-proformas`);
}

export async function updateOpportunityStage(
  id,
  stage,
  reason = 'Cambio de etapa desde pipeline'
) {
  return apiFetch(`/opportunities/${id}/stage`, {
    method: 'PATCH',
    body: JSON.stringify({
      stage,
      reason,
    }),
  });
}

export async function convertOpportunityToCustomer(id) {
  return apiFetch(`/opportunities/${id}/convert-to-customer`, {
    method: 'POST',
  });
}