import { apiFetch } from './api';

export function getProformaAttachments(proformaId) {
  return apiFetch(`/proformas/${proformaId}/attachments`);
}

export function createProformaAttachment(proformaId, payload) {
  return apiFetch(`/proformas/${proformaId}/attachments`, {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function deleteProformaAttachment(proformaId, attachmentId) {
  return apiFetch(`/proformas/${proformaId}/attachments/${attachmentId}`, {
    method: 'DELETE',
  });
}