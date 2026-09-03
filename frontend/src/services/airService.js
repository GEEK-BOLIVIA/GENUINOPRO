import { apiFetch, getApiToken } from './api';

export async function getAirProformas() {
  const data = await apiFetch(
    '/typed-proformas/air'
  );

  const items = Array.isArray(data)
    ? data
    : data?.items || data?.data || [];

  return items.map((item) => ({
    id: item.id,
    type: 'AEREO',

    customerId: item.customerId,
    opportunityId: item.opportunityId,

    code: item.code || item.id,

    client:
      item.input?.customerName ||
      'Sin cliente',

    createdAt: item.createdAt,

    issueDate:
      item.input?.issueDate,

    sellerName:
      item.input?.sellerName || '-',

    amount: `BOB ${Number(
      item.calculation?.totalBob || 0
    ).toLocaleString('es-BO')}`,

    rawStatus:
      item.status || 'DRAFT',

    status:
      normalizeAirStatus(item.status),

    total:
      item.calculation?.totalBob || 0,
  }));
}

export async function calculateAirProforma(
  payload
) {
  return apiFetch(
    '/typed-proformas/air/calculate',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    }
  );
}

export async function createAirProforma(
  payload
) {
  return apiFetch(
    '/typed-proformas/air',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    }
  );
}

export async function getAirProformaById(id) {
  return apiFetch(
    `/typed-proformas/air/${id}`
  );
}

export async function updateAirProforma(
  id,
  payload
) {
  return apiFetch(
    `/typed-proformas/air/${id}`,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    }
  );
}

export async function submitAirForReview(id) {
  return apiFetch(
    `/typed-proformas/air/${id}/submit-review`,
    {
      method: 'POST',
    }
  );
}

export async function approveAirProforma(id) {
  return apiFetch(
    `/typed-proformas/air/${id}/approve`,
    {
      method: 'POST',
    }
  );
}

export async function rejectAirProforma(
  id,
  reason
) {
  return apiFetch(
    `/typed-proformas/air/${id}/reject`,
    {
      method: 'POST',
      body: JSON.stringify({
        reason,
      }),
    }
  );
}

export async function downloadAirProformaPdf(id) {
  const token = getApiToken();

  const API_BASE_URL =
    import.meta.env.VITE_API_URL || '/api';

  const response = await fetch(
    `${API_BASE_URL}/typed-proformas/air/${id}/pdf`,
    {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!response.ok) {
    const message =
      await response.text();

    throw new Error(
      message ||
      `No se pudo generar el PDF (${response.status})`
    );
  }

  return await response.blob();
}

function normalizeAirStatus(status) {
  const map = {
    DRAFT: 'Borrador',
    IN_REVIEW: 'En revisión',
    APPROVED: 'Aprobada interna',
    REJECTED: 'Rechazada',
    CLIENT_ACCEPTED: 'Aprobada cliente',
    CLIENT_REJECTED: 'Rechazada cliente',
  };

  return (
    map[status] ||
    status ||
    'Borrador'
  );
}