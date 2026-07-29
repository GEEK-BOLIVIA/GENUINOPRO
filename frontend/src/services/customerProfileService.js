import { apiFetch } from './api';

export function getBoliviaCities() {
  return apiFetch('/catalogs/bolivia-cities');
}

export function getLeadCustomerProfile(leadId) {
  return apiFetch(`/leads/${leadId}/customer-profile`);
}

export function saveLeadCustomerProfile(leadId, payload) {
  return apiFetch(`/leads/${leadId}/customer-profile`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  });
}