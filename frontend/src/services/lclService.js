import { apiFetch } from './api';

function normalizeStatus(status) {
  const map = {
    DRAFT: 'Borrador',
    IN_REVIEW: 'En revisión',
    APPROVED: 'Aprobada',
    REJECTED: 'Rechazada',
    EXPIRED: 'Vencida',
    APPROVED_BY_CUSTOMER: 'Aprobada por cliente',
    REJECTED_BY_CUSTOMER: 'Rechazada por cliente',
  };

  return map[status] || status || 'Borrador';
}

function statusColor(status) {
  const map = {
    DRAFT: 'bg-slate-100 text-slate-700',
    IN_REVIEW: 'bg-amber-50 text-amber-700',
    APPROVED: 'bg-emerald-50 text-emerald-700',
    REJECTED: 'bg-rose-50 text-rose-700',
    EXPIRED: 'bg-zinc-100 text-zinc-700',
    APPROVED_BY_CUSTOMER: 'bg-emerald-100 text-emerald-800',
    REJECTED_BY_CUSTOMER: 'bg-rose-100 text-rose-800',
  };

  return map[status] || 'bg-slate-100 text-slate-700';
}

export async function getLclProformas() {
  const data = await apiFetch('/typed-proformas/lcl');
  const items = Array.isArray(data) ? data : data.items || data.data || [];


  return items.map((item) => ({
    id: item.id,
    type: 'LCL',
    customerId: item.customerId,
    code: item.code || item.proformaNumber || item.id,
    client: item.customerName || 'Sin cliente',
    origin: item.originCity || item.portOrigin || '-',
    destination: item.destinationCity || item.portDestination || '-',
    createdAt: item.createdAt,
    issueDate: item.issueDate,
    sellerName: item.sellerName || '-',
    amount: `${item.currency || 'USD'} ${Number(item.total || 0).toLocaleString()}`,
    rawStatus: item.status,
    status: normalizeStatus(item.status),
    statusColor: statusColor(item.status),
    createdAt: item.createdAt,
    total: item.total || 0,
  }));
}

export async function getLclProformaById(id) {
  return apiFetch(`/typed-proformas/lcl/${id}`);
}

export async function createLclProforma(payload) {
  return apiFetch('/typed-proformas/lcl', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}
export async function approveLclProforma(id) {
  return apiFetch(`/typed-proformas/lcl/${id}/approve`, {
    method: 'POST',
  });
}

export async function rejectLclProforma(id) {
  return apiFetch(`/typed-proformas/lcl/${id}/reject`, {
    method: 'POST',
  });
}

export async function calculateOperationalLcl(payload) {
  return apiFetch('/typed-proformas/lcl/operational/calculate', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function createOperationalLclProforma(payload) {
  return apiFetch('/typed-proformas/lcl/operational', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function recalculateLclProforma(id, payload) {
  return apiFetch(`/typed-proformas/lcl/${id}/recalculate`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  });
}

export async function sendLclToReview(id) {
  return apiFetch(`/typed-proformas/lcl/${id}/submit-review`, {
    method: 'POST',
  });
}

export async function submitLclForReview(id) {
  return apiFetch(`/typed-proformas/lcl/${id}/submit-review`, {
    method: 'POST',
  });
}

export async function clientAcceptLclProforma(id) {
  return apiFetch(`/typed-proformas/lcl/${id}/client-accept`, {
    method: 'POST',
  });
}

export async function clientRejectLclProforma(id, reason) {
  return apiFetch(`/typed-proformas/lcl/${id}/client-reject`, {
    method: 'POST',
    body: JSON.stringify({ reason }),
  });
}

export async function updateOperationalLclProforma(id, payload) {
  return apiFetch(
    `/typed-proformas/lcl/${id}/operational`,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    }
  );
}
