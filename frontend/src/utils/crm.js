import {
  Phone,
  Mail,
  MessageSquare,
  CalendarDays,
  ClipboardList,
  Sparkles,
} from 'lucide-react';

export const STAGES = ['Descubrimiento', 'Calificación', 'Propuesta', 'Cierre'];

export function mapStage(stage) {
  if (stage === 'LEAD') return 'Descubrimiento';
  if (stage === 'CONTACTED') return 'Calificación';
  if (stage === 'PROPOSAL') return 'Propuesta';
  if (stage === 'WON') return 'Cierre';
  return 'Descubrimiento';
}

export function formatCurrency(amount) {
  return new Intl.NumberFormat('es-BO', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 0,
  }).format(Number(amount || 0));
}

export function formatActivityDate(value) {
  if (!value) return 'Sin fecha';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);

  return new Intl.DateTimeFormat('es-BO', {
    day: '2-digit',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}

export function getActivityLabel(type) {
  const labels = {
    NOTE: 'Nota',
    CALL: 'Llamada',
    EMAIL: 'Correo',
    WHATSAPP: 'WhatsApp',
    MEETING: 'Reunión',
    TASK: 'Tarea',
    LEAD_CREATED: 'Lead recibido',
    OPPORTUNITY_CREATED: 'Oportunidad creada',
    CHANGE_STAGE: 'Cambio de etapa',
    CLOSE_LOST: 'Oportunidad perdida',
    PROFORMA_CREATED: 'Proforma creada',
    PROFORMA_APPROVE: 'Proforma aprobada',
    PROFORMA_REJECT: 'Proforma rechazada',
    CONVERT_TO_CUSTOMER: 'Convertido a cliente',
  };

  return labels[type] || type || 'Actividad';
}

export function getActivityIcon(type) {
  switch (type) {
    case 'CALL':
      return Phone;
    case 'EMAIL':
      return Mail;
    case 'WHATSAPP':
      return MessageSquare;
    case 'MEETING':
      return CalendarDays;
    case 'TASK':
      return ClipboardList;
    case 'NOTE':
      return Sparkles;
    default:
      return Sparkles;
  }
}

export function getActivityBadgeClass(type, source) {
  if (source === 'MANUAL') {
    switch (type) {
      case 'CALL':
        return 'bg-blue-50 text-blue-700 ring-1 ring-inset ring-blue-200';
      case 'EMAIL':
        return 'bg-violet-50 text-violet-700 ring-1 ring-inset ring-violet-200';
      case 'WHATSAPP':
        return 'bg-emerald-50 text-emerald-700 ring-1 ring-inset ring-emerald-200';
      case 'MEETING':
        return 'bg-amber-50 text-amber-700 ring-1 ring-inset ring-amber-200';
      case 'TASK':
        return 'bg-indigo-50 text-indigo-700 ring-1 ring-inset ring-indigo-200';
      default:
        return 'bg-slate-100 text-slate-700 ring-1 ring-inset ring-slate-200';
    }
  }

  switch (type) {
    case 'LEAD_CREATED':
      return 'bg-emerald-50 text-emerald-700 ring-1 ring-inset ring-emerald-200';
    case 'OPPORTUNITY_CREATED':
      return 'bg-sky-50 text-sky-700 ring-1 ring-inset ring-sky-200';
    case 'CHANGE_STAGE':
      return 'bg-amber-50 text-amber-700 ring-1 ring-inset ring-amber-200';
    case 'CLOSE_LOST':
      return 'bg-rose-50 text-rose-700 ring-1 ring-inset ring-rose-200';
    case 'PROFORMA_CREATED':
      return 'bg-fuchsia-50 text-fuchsia-700 ring-1 ring-inset ring-fuchsia-200';
    case 'PROFORMA_APPROVE':
      return 'bg-emerald-50 text-emerald-700 ring-1 ring-inset ring-emerald-200';
    case 'PROFORMA_REJECT':
      return 'bg-rose-50 text-rose-700 ring-1 ring-inset ring-rose-200';
    case 'CONVERT_TO_CUSTOMER':
      return 'bg-indigo-50 text-indigo-700 ring-1 ring-inset ring-indigo-200';
    default:
      return 'bg-slate-100 text-slate-700 ring-1 ring-inset ring-slate-200';
  }
}

export function getActivityTypeOptions() {
  return [
    { value: 'NOTE', label: 'Nota' },
    { value: 'CALL', label: 'Llamada' },
    { value: 'EMAIL', label: 'Correo' },
    { value: 'WHATSAPP', label: 'WhatsApp' },
    { value: 'MEETING', label: 'Reunión' },
    { value: 'TASK', label: 'Tarea' },
  ];
}

export function normalizeTimelineResponse(raw) {
  const events = raw?.events ?? [];

  const normalizedEvents = events.map((event, index) => ({
    id: `${event.type || 'EV'}-${event.timestamp || index}-${index}`,
    text:
      event.reason ||
      event.title ||
      event.messagePreview ||
      getActivityLabel(event.type) ||
      'Actividad',
    title:
      event.title ||
      event.reason ||
      event.messagePreview ||
      getActivityLabel(event.type) ||
      'Actividad',
    description: event.messagePreview || event.reason || '',
    date: event.timestamp || new Date().toISOString(),
    activityDate: event.timestamp || new Date().toISOString(),
    type: event.type || 'SYSTEM',
    source: 'SYSTEM',
    createdBy: event.actorUserId || event.assignedSellerId || event.ownerUserId || 'system',
  }));

  const activitiesRaw = Array.isArray(raw) ? raw : raw?.activities || [];

  const normalizedActivities = activitiesRaw.map((item) => ({
    id: item.id ? `ACT-${item.id}` : `ACT-${item.title || 'manual'}`,
    text: item.title || item.description || item.type || 'Actividad',
    title: item.title || getActivityLabel(item.type) || 'Actividad',
    description: item.description || '',
    date: item.activityDate || item.createdAt || new Date().toISOString(),
    activityDate: item.activityDate || item.createdAt || new Date().toISOString(),
    type: item.type || 'NOTE',
    source: item.source || 'MANUAL',
    createdBy: item.createdBy || 'system',
  }));

  return [...normalizedEvents, ...normalizedActivities].sort(
    (a, b) => new Date(b.date) - new Date(a.date)
  );
}