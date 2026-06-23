import { apiFetch } from './api';

export async function getProformaRates(
  proformaType = 'LCL',
  includeInactive = false
) {
  return apiFetch(
    `/parameters/proforma-rates?proformaType=${proformaType}&includeInactive=${includeInactive}`
  );
}

export async function createProformaRate(payload) {
  return apiFetch('/parameters/proforma-rates', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function updateProformaRate(id, payload) {
  return apiFetch(`/parameters/proforma-rates/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  });
}

export async function deleteProformaRate(id) {
  return apiFetch(`/parameters/proforma-rates/${id}`, {
    method: 'DELETE',
  });
}

export async function activateProformaRate(id) {
  return apiFetch(`/parameters/proforma-rates/${id}/activate`, {
    method: 'PATCH',
  });
}