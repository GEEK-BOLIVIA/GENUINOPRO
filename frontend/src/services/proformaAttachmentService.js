import { apiFetch, getApiToken } from './api';

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

export async function uploadProformaAttachmentImage(proformaId, formData) {
  const response = await fetch(
    `http://localhost:8081/api/proformas/${proformaId}/attachments/image`,
    {
      method: 'POST',
      body: formData,
      headers: {
        ...(getApiToken()
          ? { Authorization: `Bearer ${getApiToken()}` }
          : {}),
      },
    }
  );

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || 'No se pudo subir la imagen.');
  }

  return response.json();
}