import { apiFetch } from './api';
import { getApiToken } from './api';

export async function getFclProformas() {
  const data = await apiFetch('/typed-proformas/fcl');

  const items = Array.isArray(data) ? data : data.items || data.data || [];

  return items.map((item) => {
    const total =
      item.totalOperationBob ??
      item.totalBob ??
      item.subtotalBob ??
      item.cifBob ??
      item.total ??
      0;

    return {
      id: item.id,
      type: 'FCL',
      customerId: item.customerId,
      opportunityId: item.opportunityId,
      code: item.code || item.proformaNumber || item.id,
      client: item.customerName || 'Sin cliente',
      origin: item.originCity || item.originPort || '-',
      destination: item.destinationCity || '-',
      createdAt: item.createdAt,
      issueDate: item.issueDate,
      sellerName: item.sellerName || '-',
      amount: `${item.currency || 'BOB'} ${Number(total || 0).toLocaleString()}`,
      rawStatus: item.status || 'DRAFT',
      status: normalizeFclStatus(item.status),
      statusColor: fclStatusColor(item.status),
      total,
    };
  });
}

export async function getFclProformaById(id) {
  const response = await apiFetch(
    `/typed-proformas/fcl/${id}`
  );

  return {
    ...(response?.proforma || {}),
    rejectionReason:
      response?.rejectionReason || null,
  };
}

export async function createFclProforma(payload) {
  return apiFetch('/typed-proformas/fcl', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function updateFclProforma(id, payload) {
  return apiFetch(`/typed-proformas/fcl/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  });
}

export async function calculateFclProforma(payload) {
  return apiFetch('/typed-proformas/fcl/calculate', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function downloadFclPdf(id) {
  const token = getApiToken();

  const response = await fetch(
    `/api/typed-proformas/fcl/${id}/pdf`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!response.ok) {
    throw new Error('No se pudo descargar el PDF FCL');
  }

  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');

  link.href = url;
  link.download = `proforma-fcl-${id}.pdf`;

  document.body.appendChild(link);
  link.click();
  link.remove();

  window.URL.revokeObjectURL(url);
}

export async function submitFclForReview(id) {
  return apiFetch(`/typed-proformas/fcl/${id}/submit-review`, {
    method: 'POST',
  });
}

export async function approveFclProforma(id) {
  return apiFetch(`/typed-proformas/fcl/${id}/approve`, {
    method: 'POST',
  });
}

export async function rejectFclProforma(
  id,
  reason
) {
  return apiFetch(
    `/typed-proformas/fcl/${id}/reject`,
    {
      method: 'POST',
      body: JSON.stringify({
        reason,
      }),
    }
  );
}

export async function approveFclByCustomer(id) {
  return apiFetch(
    `/typed-proformas/fcl/${id}/approve-customer`,
    {
      method: 'POST',
    }
  );
}

function normalizeFclStatus(status) {
  const map = {
    DRAFT: 'Borrador',
    IN_REVIEW: 'En revisión',
    APPROVED: 'Aprobada interna',
    REJECTED: 'Rechazada',
    APPROVED_BY_CUSTOMER: 'Aprobada cliente',
    REJECTED_BY_CUSTOMER: 'Rechazada cliente',
  };

  return map[status] || status || 'Borrador';
}

function fclStatusColor(status) {
  const map = {
    DRAFT: 'bg-slate-100 text-slate-700',
    IN_REVIEW: 'bg-blue-50 text-blue-700',
    APPROVED: 'bg-emerald-50 text-emerald-700',
    REJECTED: 'bg-rose-50 text-rose-700',
    APPROVED_BY_CUSTOMER: 'bg-green-100 text-green-800',
    REJECTED_BY_CUSTOMER: 'bg-red-100 text-red-800',
  };

  return map[status] || 'bg-slate-100 text-slate-700';
}