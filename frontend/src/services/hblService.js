import { apiFetch, getApiToken } from './api';

export async function getHblProformas() {
  const data = await apiFetch(
    '/typed-proformas/hbl'
  );

  const items = Array.isArray(data)
    ? data
    : data?.items || data?.data || [];

  return items.map((item) => ({
    id: item.id,
    type: 'HBL',

    customerId: item.customerId,
    opportunityId: item.opportunityId,

    code:
      item.code ||
      item.id,

    client:
      item.customerName ||
      item.input?.customerName ||
      'Sin cliente',

    origin:
      item.originCity ||
      '-',

    destination:
      item.destinationCity ||
      '-',

    createdAt:
      item.createdAt,

    issueDate:
      item.issueDate ||
      item.input?.issueDate,

    sellerName:
      item.sellerName ||
      item.input?.sellerName ||
      '-',

    amount: `BOB ${Number(
      item.total ||
      item.calculation?.totalBob ||
      0
    ).toLocaleString('es-BO')}`,

    rawStatus:
      item.status || 'DRAFT',

    status:
      normalizeHblStatus(
        item.status
      ),

    total:
      item.total ||
      item.calculation?.totalBob ||
      0,
  }));
}

export async function calculateHblProforma(
  payload
) {
  return apiFetch(
    '/typed-proformas/hbl/calculate',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    }
  );
}

export async function createHblProforma(
  payload
) {
  return apiFetch(
    '/typed-proformas/hbl',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    }
  );
}

export async function getHblProformaById(
  id
) {
  return apiFetch(
    `/typed-proformas/hbl/${id}`
  );
}

export async function updateHblProforma(
  id,
  payload
) {
  return apiFetch(
    `/typed-proformas/hbl/${id}`,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    }
  );
}

export async function submitHblForReview(
  id
) {
  return apiFetch(
    `/typed-proformas/hbl/${id}/submit-review`,
    {
      method: 'POST',
    }
  );
}

export async function approveHblProforma(
  id
) {
  return apiFetch(
    `/typed-proformas/hbl/${id}/approve`,
    {
      method: 'POST',
    }
  );
}

export async function rejectHblProforma(
  id,
  reason
) {
  return apiFetch(
    `/typed-proformas/hbl/${id}/reject`,
    {
      method: 'POST',
      body: JSON.stringify({
        reason,
      }),
    }
  );
}

export async function downloadHblPdf(id) {
  const token = getApiToken();

  const response = await fetch(
    `/api/typed-proformas/hbl/${id}/pdf`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!response.ok) {
    throw new Error(
      'No se pudo descargar el PDF HBL.'
    );
  }

  const contentType =
    response.headers.get('content-type') || '';

  if (!contentType.includes('application/pdf')) {
    const text = await response.text();

    console.error(
      'Respuesta inesperada del PDF HBL:',
      text
    );

    throw new Error(
      'El servidor no devolvió un documento PDF.'
    );
  }

  const blob = await response.blob();

  const url =
    window.URL.createObjectURL(blob);

  const link =
    document.createElement('a');

  link.href = url;
  link.download =
    `proforma-HBL-${id}.pdf`;

  document.body.appendChild(link);

  link.click();
  link.remove();

  setTimeout(() => {
    window.URL.revokeObjectURL(url);
  }, 1000);
}

function normalizeHblStatus(status) {
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