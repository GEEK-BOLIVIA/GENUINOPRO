import {
  Phone,
  Building2,
  User,
  Clock3,
  MessageSquare,
  BadgeCheck,
  CalendarCheck,
  FileText,
  Search,
  ChevronRight,
} from 'lucide-react';

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  createLeadActivity,
} from '../../services/leadActivitiesService';

import {
  createLeadTask,
  completeTask,
} from '../../services/tasksService';

import { getCommercialSummary } from '../../services/commercialSummaryService';

import {
  getBoliviaCities,
  getLeadCustomerProfile,
  saveLeadCustomerProfile,
} from '../../services/customerProfileService';


const statusLabels = {
  NEW: 'Nuevo Contacto',
  CONTACTED: 'Contactado',
  NEGOTIATION: 'Requiere cotización',
  QUOTED: 'Proforma enviada',
  WON: 'Cliente',
  LOST: 'Descartado',
};

const activityLabels = {
  CREATE: 'Contacto creado',
  NOTE: 'Seguimiento comercial',
  PROFORMA_CREATED: 'Proforma generada',
  SUBMITTED_FOR_APPROVAL: 'Proforma enviada a revisión',
  APPROVED: 'Proforma aprobada internamente',
  REJECTED: 'Proforma rechazada internamente',
  CUSTOMER_APPROVED: 'Cliente aprobó proforma',
  CUSTOMER_REJECTED: 'Cliente rechazó proforma',
  CLIENT_CREATED: 'Cliente creado en sistema',
};

const proformaStatusLabels = {
  DRAFT: 'Borrador',
  IN_REVIEW: 'En revisión interna',
  APPROVED: 'Aprobada internamente',
  REJECTED: 'Rechazada internamente',
  SENT_TO_CLIENT: 'Enviada al cliente',
  CLIENT_ACCEPTED: 'Cliente aprobó',
  CLIENT_REJECTED: 'Cliente rechazó',
  APPROVED_BY_CUSTOMER: 'Cliente aprobó',
  REJECTED_BY_CUSTOMER: 'Cliente rechazó',
};

function formatDateTime(value) {
  if (!value) return { date: '-', time: '-' };

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return { date: '-', time: '-' };
  }

  return {
    date: date.toLocaleDateString(),
    time: date.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
    }),
  };
}

function formatMoney(value, currency = 'BOB') {
  if (value === null || value === undefined || value === '') {
    return '-';
  }

  const number = Number(value);

  if (Number.isNaN(number)) {
    return String(value);
  }

  return `${currency} ${number.toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

function getProformaProduct(proforma) {
  return (
    proforma.product ||
    proforma.productName ||
    proforma.merchandise ||
    proforma.goodsDescription ||
    proforma.description ||
    'Producto no especificado'
  );
}

function getProformaTotal(proforma) {
  return (
    proforma.total ||
    proforma.amount ||
    proforma.totalBob ||
    proforma.totalBs ||
    proforma.finalTotal ||
    null
  );
}

function ProformaStatusBadge({ status, label }) {
  const styles = {
    DRAFT: 'bg-amber-100 text-amber-700',
    IN_REVIEW: 'bg-blue-100 text-blue-700',
    APPROVED: 'bg-emerald-100 text-emerald-700',
    REJECTED: 'bg-red-100 text-red-700',
    SENT_TO_CLIENT: 'bg-violet-100 text-violet-700',
    CLIENT_ACCEPTED: 'bg-emerald-100 text-emerald-700',
    CLIENT_REJECTED: 'bg-red-100 text-red-700',
    APPROVED_BY_CUSTOMER: 'bg-emerald-100 text-emerald-700',
    REJECTED_BY_CUSTOMER: 'bg-red-100 text-red-700',
  };

  const className =
    styles[status] || 'bg-slate-100 text-slate-700';

  return (
    <span
      className={`rounded-full px-3 py-1 text-xs font-bold ${className}`}
    >
      {label}
    </span>
  );
}

const timelineLabels = {
  CREATE: 'Contacto creado',
  CHANGE_STAGE: 'Cambio de etapa comercial',
  PROFORMA_SUBMITTED_FOR_APPROVAL: 'Proforma enviada a revisión',
  PROFORMA_APPROVED: 'Proforma aprobada internamente',
  PROFORMA_REJECTED: 'Proforma rechazada internamente',
  PROFORMA_CLIENT_ACCEPTED: 'Cliente aceptó la proforma',
  PROFORMA_CLIENT_REJECTED: 'Cliente rechazó la proforma',
  TASK_CREATED: 'Tarea comercial creada',
  ACTIVITY_CREATED: 'Seguimiento registrado',
  TASK_COMPLETED: 'Tarea completada',
};


export default function LeadDetailsModal({ lead, open, onClose }) {
  
  const [summary, setSummary] = useState(null);
  const [activities, setActivities] = useState([]);
  const [activityText, setActivityText] = useState('');
  
  const [proformas, setProformas] = useState([]);
  
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const [tasks, setTasks] = useState([]);
  const [taskTitle, setTaskTitle] = useState('');
  const [taskDescription, setTaskDescription] = useState('');
  const [taskDueAt, setTaskDueAt] = useState('');
  const [savingTask, setSavingTask] = useState(false);
  const [proformaGroups, setProformaGroups] = useState([]);

  const [relatedLeads, setRelatedLeads] = useState([]);
  const [selectedLeadId, setSelectedLeadId] = useState(null);
  const [leadSearch, setLeadSearch] = useState('');

  const [customerProfile, setCustomerProfile] = useState(null);
  const [cities, setCities] = useState([]);
  const [editingProfile, setEditingProfile] = useState(false);
  const [savingProfile, setSavingProfile] = useState(false);

  useEffect(() => {
    if (!open || !lead?.id) return;

    async function loadContact360() {
      try {
        setIsLoading(true);
        setActivityText('');
        setTaskTitle('');

        const summaryData = await getCommercialSummary(lead.id);

        setSummary(summaryData || null);
        setActivities(Array.isArray(summaryData?.timeline) ? summaryData.timeline : []);
        setTasks(Array.isArray(summaryData?.tasks) ? summaryData.tasks : []);
        setProformas(Array.isArray(summaryData?.proformas) ? summaryData.proformas : []);

        const related = Array.isArray(summaryData?.relatedLeads)
          ? summaryData.relatedLeads
          : [];

        setRelatedLeads(related);

        setSelectedLeadId(null);

        setProformaGroups(
          Array.isArray(summaryData?.proformaGroups)
            ? summaryData.proformaGroups
            : []
        );



      } catch (error) {
        console.error('Error cargando Contacto 360', error);
        setSummary(null);
        setActivities([]);
        setTasks([]);
        setProformas([]);
      } finally {
        setIsLoading(false);
      }
    }

    loadContact360();
  }, [open, lead?.id]);

  useEffect(() => {
    if (!open || !lead?.id) return;

    const profileLeadId =
      selectedLeadId ||
      summary?.lead?.id ||
      lead.id;

    async function loadCustomerProfile() {
      try {
        const [profileData, citiesData] =
          await Promise.all([
            getLeadCustomerProfile(profileLeadId),
            getBoliviaCities(),
          ]);

        setCustomerProfile(profileData || null);
        setCities(
          Array.isArray(citiesData)
            ? citiesData
            : []
        );
      } catch (error) {
        console.error(
          'Error cargando perfil del cliente',
          error
        );

        setCustomerProfile(null);
      }
    }

    loadCustomerProfile();
  }, [
    open,
    lead?.id,
    selectedLeadId,
    summary?.lead?.id,
  ]);

async function handleSaveCustomerProfile(payload) {
  const profileLeadId =
    selectedLeadId ||
    summary?.lead?.id ||
    lead?.id;

  if (!profileLeadId) {
    alert('No se identificó el lead.');
    return;
  }

  try {
    setSavingProfile(true);

    const saved =
      await saveLeadCustomerProfile(
        profileLeadId,
        payload
      );

    setCustomerProfile(saved);
    setEditingProfile(false);
  } catch (error) {
    console.error(
      'Error guardando perfil del cliente',
      error
    );

    alert(
      error.message ||
        'No se pudo guardar el perfil.'
    );
  } finally {
    setSavingProfile(false);
  }
}

  if (!open || !lead) return null;

  const currentLead = summary?.lead || lead;
  const currentLeadId = selectedLeadId || currentLead?.id || lead?.id;
  async function handleAddActivity() {
    if (!activityText.trim() || !currentLeadId) return;

    try {
      const created = await createLeadActivity(currentLeadId, {
        type: 'NOTE',
        description: activityText.trim(),
        createdBy: 'admin',
      });

      setActivities((prev) => [created, ...prev]);
      setActivityText('');
    } catch (error) {
      console.error('Error creando actividad', error);
      alert('No se pudo crear la actividad.');
    }
  }

  async function handleAddTask() {
    if (!taskTitle.trim() || !currentLeadId) return;

    try {
      const created = await createLeadTask(currentLeadId, {
        title: taskTitle.trim(),
        description: '',
        priority: 'MEDIA',
        assignedTo: 'admin',
      });

      setTasks((prev) => [created, ...prev]);
      setTaskTitle('');
    } catch (error) {
      console.error('Error creando tarea', error);
      alert('No se pudo crear la tarea.');
    }
  }

  function resolveActiveOpportunityId() {
    const directOpportunityId = summary?.opportunity?.id;

    if (directOpportunityId && String(directOpportunityId).startsWith('opp_')) {
      return directOpportunityId;
    }

    return null;
  }

  function buildProformaRoute(type) {
    const leadId = currentLead?.id || lead?.id;
    const opportunityId = resolveActiveOpportunityId();

    if (!leadId) {
      alert('No se encontró el contacto activo.');
      return null;
    }

    if (!opportunityId) {
      alert('No se encontró la oportunidad comercial asociada a este requerimiento.');
      return null;
    }

    const params = new URLSearchParams({
      leadId,
      opportunityId,
    });

    const routes = {
      LCL: `/lcl/nueva?${params.toString()}`,
      FCL: `/fcl/nueva?${params.toString()}`,
      HBL: `/hbl/nueva?${params.toString()}`,
      AEREO: `/air/nueva?${params.toString()}`,
      CUSTOM: `/proformas/nueva?${params.toString()}`,
    };

    return routes[type] || null;
  }

  function handleCreateLcl() {
    const route = buildProformaRoute('LCL');

    if (!route) return;

    navigate(route);
    onClose?.();
  }

  function handleCreateProforma(type) {
    const route = buildProformaRoute(type);

    if (!route) return;

    navigate(route);
    onClose?.();
  }

  const groupedActivities = activities.reduce((groups, activity) => {
  const dateValue =
    activity.createdAt ||
    activity.timestamp ||
    new Date().toISOString();

  const date = new Date(dateValue);

  const groupKey = date.toLocaleDateString('es-BO', {
    year: 'numeric',
    month: 'long',
  });

  if (!groups[groupKey]) {
    groups[groupKey] = [];
  }

  groups[groupKey].push(activity);

  return groups;
}, {});

const overdueTasks = tasks.filter((task) => {
  const dueDate = task.dueAt ? new Date(task.dueAt) : null;
  const isPending = (task.status || '').toUpperCase() === 'PENDING';

  return isPending && dueDate && dueDate < new Date();
});

const pendingTasks = tasks.filter((task) => {
  const dueDate = task.dueAt ? new Date(task.dueAt) : null;
  const isPending = (task.status || '').toUpperCase() === 'PENDING';

  return isPending && (!dueDate || dueDate >= new Date());
});

const completedTasks = tasks.filter((task) => {
  return (
    (task.status || '').toUpperCase() === 'COMPLETED'
  );
});

const filteredRelatedLeads = relatedLeads.filter((item) => {
  const text = [
    item.messagePreview,
    item.fullName,
    item.status,
    item.source,
  ]
    .filter(Boolean)
    .join(' ')
    .toLowerCase();

  return text.includes(
    leadSearch.toLowerCase()
  );
});

const groupedRelatedLeads =
  filteredRelatedLeads.reduce((groups, item) => {

    const year = item.year || 'Sin gestión';
    const month = item.monthLabel || 'Sin mes';

    const key = `${year}-${month}`;

    if (!groups[key]) {
      groups[key] = {
        year,
        month,
        items: [],
      };
    }

    groups[key].items.push(item);

    return groups;
  }, {});

function renderTask(task) {
  const dueDate = task.dueAt ? new Date(task.dueAt) : null;
  const isPending = (task.status || '').toUpperCase() === 'PENDING';
  const isCompleted = (task.status || '').toUpperCase() === 'COMPLETED';
  const isOverdue = dueDate && isPending && dueDate < new Date();

  const created = formatDateTime(task.createdAt);
  const due = formatDateTime(task.dueAt);
  const completed = formatDateTime(task.completedAt);

  return (
    <div
      key={task.id || task.taskId || `${task.title}-${task.dueAt}`}
      className={`rounded-2xl border p-5 ${
        isCompleted
          ? 'border-emerald-200 bg-emerald-50'
          : isOverdue
            ? 'border-rose-200 bg-rose-50'
            : 'border-amber-200 bg-amber-50'
      }`}
    >
      <div className="flex items-start justify-between gap-4">
        <div>
          <div className="mb-3 space-y-1">
            {task.createdAt && (
              <p className="text-xs text-slate-400">
                Creada: {created.date} {created.time}
              </p>
            )}

            {task.dueAt && !isCompleted && (
              <p className="text-xs font-semibold text-amber-600">
                Vence: {due.date} {due.time}
              </p>
            )}

            {task.completedAt && (
              <p className="text-xs font-semibold text-emerald-600">
                Completada: {completed.date} {completed.time}
              </p>
            )}
          </div>

          <p className="font-bold text-slate-900">
            {task.title || 'Tarea comercial'}
          </p>

          {task.description && (
            <p className="mt-1 text-sm text-slate-500">{task.description}</p>
          )}

          <p className="mt-2 text-sm text-slate-500">
            Responsable: {task.assignedTo || 'Sin asignar'}
          </p>

          <p className="text-sm text-slate-400">
            Prioridad: {task.priority || '-'}
          </p>
          {!isCompleted && (
          <button
            onClick={() =>
              handleCompleteTask(task.id)
            }
            className="mt-3 rounded-xl bg-emerald-600 px-3 py-2 text-xs font-bold text-white hover:bg-emerald-700"
          >
            Marcar como realizada
          </button>
        )}
        </div>

        <span
          className={`rounded-full px-3 py-1 text-xs font-bold ${
            isCompleted
              ? 'bg-emerald-100 text-emerald-700'
              : isOverdue
                ? 'bg-rose-100 text-rose-700'
                : 'bg-amber-100 text-amber-700'
          }`}
        >
          {isCompleted ? 'Completada' : isOverdue ? 'Vencida' : 'Pendiente'}
        </span>
      </div>

      {dueDate && (
        <p className="mt-3 border-t border-slate-200 pt-3 text-xs font-semibold text-slate-500">
          Vencimiento: {dueDate.toLocaleDateString()} {dueDate.toLocaleTimeString()}
        </p>
      )}
    </div>
  );
}

async function handleCompleteTask(taskId) {
  try {
    const updated = await completeTask(taskId);

    setTasks((prev) =>
      prev.map((task) =>
        task.id === updated.id ? updated : task
      )
    );
  } catch (error) {
    console.error('Error completando tarea', error);
    alert('No se pudo completar la tarea.');
  }
}

async function handleSelectRelatedLead(relatedLeadId) {
  try {
    setIsLoading(true);
    setSelectedLeadId(relatedLeadId);

    const summaryData = await getCommercialSummary(relatedLeadId);

    setSummary(summaryData || null);
    setActivities(Array.isArray(summaryData?.timeline) ? summaryData.timeline : []);
    setTasks(Array.isArray(summaryData?.tasks) ? summaryData.tasks : []);
    setProformas(Array.isArray(summaryData?.proformas) ? summaryData.proformas : []);
    setRelatedLeads(Array.isArray(summaryData?.relatedLeads) ? summaryData.relatedLeads : []);
    setProformaGroups(Array.isArray(summaryData?.proformaGroups) ? summaryData.proformaGroups : []);
  } catch (error) {
    console.error('Error cargando requerimiento seleccionado', error);
    alert('No se pudo cargar el requerimiento seleccionado.');
  } finally {
    setIsLoading(false);
  }
}

const selectedRequirement = selectedLeadId
  ? relatedLeads.find((item) => String(item.id) === String(selectedLeadId)) || currentLead
  : null;

const selectedRequirementDate = selectedRequirement
  ? formatDateTime(selectedRequirement.receivedAt || selectedRequirement.createdAt)
  : null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center overflow-y-auto bg-slate-950/40 p-6 backdrop-blur-sm">
      <div className="relative max-h-[92vh] w-full max-w-5xl overflow-y-auto rounded-[32px] bg-white p-8 shadow-2xl">
        <div className="flex items-start justify-between gap-6">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.3em] text-slate-400">
              Contacto 360
            </p>

            <h2 className="mt-2 text-4xl font-black text-slate-900">
              {currentLead.company || currentLead.contact || currentLead.fullName || 'Contacto sin nombre'}
            </h2>

            <p className="mt-2 text-slate-500">
              Gestor de relacionamiento comercial por requerimiento independiente.
            </p>
          </div>

          <button
            onClick={onClose}
            className="rounded-2xl border border-slate-200 px-5 py-3 font-semibold text-slate-600 transition hover:bg-slate-100"
          >
            Cerrar
          </button>
        </div>

        <div className="mt-8 grid gap-5 md:grid-cols-3">
          <InfoCard
              icon={
                customerProfile?.customerType === 'COMPANY'
                  ? <Building2 size={18} />
                  : <User size={18} />
              }
              title={
                customerProfile?.customerType === 'COMPANY'
                  ? 'Empresa'
                  : 'Persona natural'
              }
              value={
                customerProfile?.customerType === 'COMPANY'
                  ? customerProfile?.legalName || currentLead.company || '-'
                  : customerProfile?.fullName || currentLead.fullName || currentLead.contact || '-'
              }
            />
          <InfoCard icon={<User size={18} />} title="Contacto" value={currentLead.contact || currentLead.fullName || '-'} />
          <InfoCard icon={<Phone size={18} />} title="Teléfono" value={currentLead.phone || '-'} />
          <InfoCard icon={<BadgeCheck size={18} />} title="Estado comercial" value={statusLabels[currentLead.status] || currentLead.status || summary?.opportunity?.stage || '-'} />
          <InfoCard icon={<MessageSquare size={18} />} title="Canal" value={currentLead.channel || '-'} />
          <InfoCard icon={<Clock3 size={18} />} title="Origen" value={currentLead.source || 'Whapify / Comercial'} />
        </div>

        <div className="mt-6">
          <LeadCustomerProfilePanel
            profile={customerProfile}
            cities={cities}
            editing={editingProfile}
            saving={savingProfile}
            onEdit={() => setEditingProfile(true)}
            onCancel={() => setEditingProfile(false)}
            onSave={handleSaveCustomerProfile}
          />
        </div>

          <div className="rounded-[28px] border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
              <div>
                <p className="text-xs font-black uppercase tracking-[0.28em] text-orange-500">
                  Enterprise CRM
                </p>
                <h3 className="mt-1 text-xl font-black text-slate-900">
                  Gestión de relacionamiento
                </h3>
                <p className="mt-1 text-sm text-slate-500">
                  Cada requerimiento tiene su propio timeline, tareas y proformas.
                </p>
              </div>

              <div className="flex items-center gap-2 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 lg:w-80">
                <Search size={16} className="text-slate-400" />
                <input
                  type="text"
                  value={leadSearch}
                  onChange={(e) => setLeadSearch(e.target.value)}
                  placeholder="Buscar papel, imprenta, moto..."
                  className="w-full bg-transparent text-sm outline-none"
                />
              </div>
            </div>

            <div className="mt-6 max-h-[360px] space-y-6 overflow-y-auto pr-1">
              {Object.values(groupedRelatedLeads).length === 0 ? (
                <EmptyMessage text="No se encontraron requerimientos para este contacto." />
              ) : (
                Object.values(groupedRelatedLeads).map((group) => (
                  <div key={`${group.year}-${group.month}`}>
                    <div className="mb-3 flex items-center gap-3">
                      <div className="h-px flex-1 bg-slate-200" />
                      <p className="text-xs font-black uppercase tracking-[0.25em] text-slate-400">
                        {group.month} {group.year}
                      </p>
                    </div>

                    <div className="space-y-3">
                      {group.items.map((item) => {
                        const isSelected = selectedLeadId === item.id;
                        const itemDate = formatDateTime(item.receivedAt || item.createdAt);

                        return (
                          <button
                            key={item.id}
                            onClick={() => handleSelectRelatedLead(item.id)}
                            className={`w-full rounded-2xl border p-4 text-left transition ${
                              isSelected
                                ? 'border-orange-400 bg-orange-50 shadow-sm'
                                : 'border-slate-200 bg-white hover:border-orange-200 hover:bg-slate-50'
                            }`}
                          >
                            <div className="flex items-start justify-between gap-4">
                              <div className="min-w-0">
                                <div className="flex flex-wrap items-center gap-2">
                                  <span className={`rounded-full px-3 py-1 text-xs font-black ${
                                    isSelected ? 'bg-orange-500 text-white' : 'bg-slate-100 text-slate-600'
                                  }`}>
                                    {statusLabels[item.status] || item.status || 'Contacto'}
                                  </span>
                                  <span className="text-xs font-semibold text-slate-400">
                                    {itemDate.date} · {itemDate.time}
                                  </span>
                                </div>

                                <p className="mt-3 line-clamp-2 font-black text-slate-900">
                                  {item.messagePreview || 'Requerimiento sin descripción'}
                                </p>

                                <p className="mt-1 text-xs text-slate-500">
                                  {item.source || 'Origen comercial'} · {item.channel || 'Canal no definido'}
                                </p>
                              </div>

                              <ChevronRight
                                size={18}
                                className={isSelected ? 'text-orange-500' : 'text-slate-300'}
                              />
                            </div>
                          </button>
                        );
                      })}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

        {!selectedRequirement ? (
          <div className="mt-8 rounded-[28px] border border-dashed border-slate-300 bg-slate-50 p-8 text-center">
            <p className="text-xs font-black uppercase tracking-[0.25em] text-slate-400">
              Selección pendiente
            </p>
            <h3 className="mt-3 text-2xl font-black text-slate-900">
              Seleccione un requerimiento
            </h3>
            <p className="mx-auto mt-2 max-w-xl text-sm leading-relaxed text-slate-500">
              Al elegir un requerimiento se desplegarán su timeline, tareas y proformas independientes.
            </p>
          </div>
        ) : (
          <>
            <div className="mt-8 rounded-[30px] border border-orange-200 bg-orange-50 p-6 shadow-sm">
              <div className="flex flex-col gap-5 lg:flex-row lg:items-start lg:justify-between">
                <div className="min-w-0">
                  <p className="text-xs font-black uppercase tracking-[0.28em] text-orange-600">
                    Requerimiento activo
                  </p>
                  <h3 className="mt-2 text-2xl font-black text-slate-900">
                    {selectedRequirement.messagePreview || 'Requerimiento sin descripción'}
                  </h3>

                  <div className="mt-4 flex flex-wrap gap-2 text-xs font-bold text-slate-600">
                    <span className="rounded-full bg-white px-3 py-1">
                      {statusLabels[selectedRequirement.status] || selectedRequirement.status || 'Contacto'}
                    </span>
                    <span className="rounded-full bg-white px-3 py-1">
                      {selectedRequirement.source || 'Origen comercial'} · {selectedRequirement.channel || 'Canal no definido'}
                    </span>
                    <span className="rounded-full bg-white px-3 py-1">
                      {selectedRequirementDate?.date} · {selectedRequirementDate?.time}
                    </span>
                    <span className="rounded-full bg-white px-3 py-1">
                      Responsable: {selectedRequirement.assignedSellerName || selectedRequirement.assignedSellerId || 'Sin asignar'}
                    </span>
                  </div>
                </div>

                <div className="rounded-2xl bg-white px-4 py-3 text-sm font-bold text-orange-600">
                  ID oportunidad #{resolveActiveOpportunityId() || 'Sin oportunidad'}
                </div>
              </div>
            </div>

            <div className="mt-6 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
              <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                <div>
                  <p className="text-xs font-black uppercase tracking-[0.25em] text-orange-500">
                    Acción comercial
                  </p>
                  <h4 className="mt-1 text-xl font-black text-slate-900">
                    Nueva proforma del requerimiento
                  </h4>
                  <p className="mt-1 text-sm text-slate-500">
                    La cotización quedará asociada únicamente al requerimiento seleccionado.
                  </p>
                </div>

                <div className="grid grid-cols-2 gap-3 sm:grid-cols-5 lg:min-w-[650px]">
                  <button
                    onClick={() => handleCreateProforma('LCL')}
                    className="rounded-2xl bg-orange-500 px-4 py-3 text-sm font-bold text-white hover:bg-orange-600"
                  >
                    LCL
                  </button>

                  <button
                    onClick={() => handleCreateProforma('FCL')}
                    className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm font-bold text-slate-400"
                  >
                    FCL
                  </button>

                  <button
                    onClick={() => handleCreateProforma('HBL')}
                    className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm font-bold text-slate-600 hover:border-orange-300 hover:bg-orange-50 hover:text-orange-600"
                  >
                    HBL
                  </button>

                  <button
                    onClick={() => handleCreateProforma('AEREO')}
                    className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm font-bold text-slate-400"
                  >
                    Aérea
                  </button>

                  <button
                    onClick={() => handleCreateProforma('CUSTOM')}
                    className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm font-bold text-slate-400"
                  >
                    Personalizada
                  </button>
                </div>
              </div>
            </div>

            <div className="mt-8 rounded-3xl border border-slate-200 bg-slate-50 p-6">
              <p className="text-sm font-bold uppercase tracking-wide text-slate-500">
                Mensaje inicial del requerimiento
              </p>

              <p className="mt-4 text-lg leading-relaxed text-slate-700">
                {currentLead.messagePreview || 'Sin mensaje inicial registrado.'}
              </p>
            </div>

            <div className="mt-8 grid gap-6 lg:grid-cols-2">
          <section className="rounded-3xl border border-slate-200 p-6">
            <div className="flex items-center justify-between">
              <h3 className="text-xl font-black text-slate-900">
                Timeline del requerimiento
              </h3>

              <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-bold text-emerald-700">
                {activities.length} registros
              </span>
            </div>

            <div className="mt-6 flex gap-3">
              <input
                value={activityText}
                onChange={(e) => setActivityText(e.target.value)}
                placeholder="Registrar nota o seguimiento..."
                className="flex-1 rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
              />

              <button
                type="button"
                onClick={handleAddActivity}
                className="rounded-2xl bg-orange-500 px-5 py-3 text-sm font-bold text-white hover:bg-orange-600"
              >
                Agregar
              </button>
            </div>

            <div className="mt-6 space-y-4">
              {isLoading ? (
                <EmptyMessage text="Cargando actividades..." />
              ) : activities.length === 0 ? (
                <EmptyMessage text="Aún no hay actividades registradas." />
              ) : (

                Object.entries(groupedActivities).map(
                  ([period, items]) => (
                    <div key={period}>
                      <div className="mb-4 mt-2">
                        <h4 className="text-xs font-black uppercase tracking-[0.25em] text-slate-400">
                          {period}
                        </h4>
                      </div>

                      <div className="space-y-4">
                        {items.map((activity, index) => {
                          const rawTitle =
                            activity.title || activity.type;

                          return (
                            <TimelineItem
                            key={`${activity.id || activity.taskId || activity.proformaId || rawTitle}-${activity.createdAt || activity.timestamp || index}-${index}`}
                              title={
                                timelineLabels[rawTitle] ||
                                timelineLabels[activity.type] ||
                                activityLabels[activity.type] ||
                                rawTitle ||
                                'Actividad comercial'
                              }
                              description={
                                activity.description &&
                                activity.description !== 'null'
                                  ? activity.description
                                  : activity.reason &&
                                    activity.reason !== 'null'
                                    ? activity.reason
                                    : activity.message &&
                                      activity.message !== 'null'
                                      ? activity.message
                                      : ''
                              }
                              createdAt={
                                activity.createdAt ||
                                activity.timestamp
                              }
                              createdBy={
                                activity.createdBy ||
                                activity.actorUserId === 'system' ||
                                activity.actorUserId?.includes('-')
                                  ? 'Sistema'
                                  : activity.actorUserId ||
                                    activity.source ||
                                    'Sistema'
                              }
                            />
                          );
                        })}
                      </div>
                    </div>
                  )
                )
              )}
            </div>
          </section>

          <section className="rounded-3xl border border-slate-200 p-6">
            <div className="flex items-center justify-between">
              <h3 className="text-xl font-black text-slate-900">
                Tareas del requerimiento
              </h3>

              <span className="rounded-full bg-orange-100 px-3 py-1 text-xs font-bold text-orange-700">
                {pendingTasks.length} pendientes
              </span>
            </div>

            <div className="mt-6 flex gap-3">
              <input
                value={taskTitle}
                onChange={(e) => setTaskTitle(e.target.value)}
                placeholder="Ej: Llamar mañana, enviar cotización..."
                className="flex-1 rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
              />

              <button
                type="button"
                onClick={handleAddTask}
                disabled={!taskTitle.trim()}
                className="rounded-2xl bg-slate-900 px-5 py-3 text-sm font-bold text-white hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-300"
              >
                Crear
              </button>
            </div>

            <div className="mt-6 space-y-3">
              {isLoading ? (
                <EmptyMessage text="Cargando tareas..." />
              ) : tasks.length === 0 ? (
                <EmptyMessage text="No hay tareas para este requerimiento." />
              ) : (
                <>
                  {overdueTasks.length > 0 && (
                    <>
                      <h4 className="text-xs font-black uppercase tracking-[0.25em] text-rose-500">
                        Tareas vencidas
                      </h4>
                      {overdueTasks.map(renderTask)}
                    </>
                  )}

                  {pendingTasks.length > 0 && (
                    <>
                      <h4 className="mt-6 text-xs font-black uppercase tracking-[0.25em] text-amber-500">
                        Tareas pendientes
                      </h4>
                      {pendingTasks.map(renderTask)}
                    </>
                  )}

                  {completedTasks.length > 0 && (
                    <>
                      <h4 className="mt-6 text-xs font-black uppercase tracking-[0.25em] text-emerald-500">
                        Tareas completadas
                      </h4>
                      {completedTasks.map(renderTask)}
                    </>
                  )}
                </>
              )}
            </div>
          </section>
        </div>

        <section className="mt-8 rounded-3xl border border-slate-200 p-6">
          <div className="flex items-center justify-between">
            <h3 className="text-xl font-black text-slate-900">
              Proformas del requerimiento
            </h3>

            <span className="rounded-full bg-blue-100 px-3 py-1 text-xs font-bold text-blue-700">
              {proformas.length} proformas
            </span>
          </div>

          <div className="mt-6 space-y-3">
            {proformas.length === 0 ? (
              <EmptyMessage text="No existen proformas para este requerimiento." />
            ) : (

              proformas.map((proforma) => {
                const created = formatDateTime(
                  proforma.createdAt || proforma.date || proforma.createdDate
                );

                const statusLabel =
                  proformaStatusLabels[proforma.status] ||
                  proforma.status ||
                  'Sin estado';

                const total = getProformaTotal(proforma);

                return (
                  <div
                    key={proforma.id}
                    className="rounded-2xl border border-slate-200 bg-slate-50 p-5"
                  >
                    <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                      <div className="min-w-0">
                        <div className="flex flex-wrap items-center gap-2">
                          <span className="rounded-full bg-orange-100 px-3 py-1 text-xs font-bold text-orange-700">
                            {proforma.type || 'LCL'}
                          </span>

                          <ProformaStatusBadge
                            status={proforma.status}
                            label={statusLabel}
                          />
                        </div>

                        <p className="mt-3 text-base font-black text-slate-900">
                          {getProformaProduct(proforma)}
                        </p>

                        <p className="mt-1 text-xs text-slate-400">
                          Código: {proforma.code || proforma.id}
                        </p>
                      </div>

                      <div className="grid gap-3 text-sm sm:grid-cols-3 lg:min-w-[380px]">
                        <div>
                          <p className="text-xs font-bold uppercase text-slate-400">
                            Fecha
                          </p>
                          <p className="font-semibold text-slate-700">
                            {created.date}
                          </p>
                          <p className="text-xs text-slate-400">
                            {created.time}
                          </p>
                        </div>

                        <div>
                          <p className="text-xs font-bold uppercase text-slate-400">
                            Total
                          </p>
                          <p className="font-semibold text-slate-700">
                            {formatMoney(total, proforma.currency || 'BOB')}
                          </p>
                        </div>

                        <div>
                          <p className="text-xs font-bold uppercase text-slate-400">
                            Proveedor
                          </p>
                          <p className="font-semibold text-slate-700">
                            {proforma.provider || proforma.supplier || '-'}
                          </p>
                        </div>
                      </div>

                      <button
                        onClick={() => {
                          const detailRoutes = {
                            LCL: `/lcl/${proforma.id}`,
                            FCL: `/fcl/${proforma.id}`,
                            HBL: `/hbl/${proforma.id}`,
                            AEREO: `/air/${proforma.id}`,
                          };

                          navigate(detailRoutes[proforma.type] || `/lcl/${proforma.id}`);
                        }}
                        className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-bold text-white"
                      >
                        Ver detalle
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </section>
          </>
        )}

      </div>
    </div>
  );
}

function InfoCard({ icon, title, value }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-slate-50 p-5">
      <div className="flex items-center gap-2 text-orange-500">{icon}</div>
      <p className="mt-3 text-sm font-semibold uppercase tracking-wide text-slate-400">
        {title}
      </p>
      <p className="mt-2 text-lg font-bold text-slate-900">{value}</p>
    </div>
  );
}

function MetricCard({ title, value }) {
  return (
    <div className="rounded-3xl border border-orange-100 bg-orange-50 p-5">
      <p className="text-xs font-bold uppercase tracking-wide text-orange-500">
        {title}
      </p>
      <p className="mt-2 text-xl font-black text-slate-900">{value}</p>
    </div>
  );
}

function ActionCard({ icon, title, description, status }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-center justify-between">
        <div className="rounded-2xl bg-orange-50 p-3 text-orange-600">
          {icon}
        </div>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-bold text-slate-600">
          {status}
        </span>
      </div>
      <h4 className="mt-5 font-black text-slate-900">{title}</h4>
      <p className="mt-2 text-sm leading-relaxed text-slate-500">{description}</p>
    </div>
  );
}

function TimelineItem({ title, description, createdAt, createdBy }) {
  const date = createdAt ? new Date(createdAt) : null;

  return (
    <div className="rounded-2xl border border-slate-200 bg-slate-50 p-5">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="font-bold text-slate-900">{title}</p>
          <p className="mt-1 text-sm text-slate-500">{description}</p>
        </div>

        {date && (
          <div className="shrink-0 text-right text-xs font-semibold text-slate-400">
            <p>{date.toLocaleDateString()}</p>
            <p>{date.toLocaleTimeString()}</p>
          </div>
        )}
      </div>

      {createdBy && (
        <p className="mt-3 border-t border-slate-200 pt-2 text-xs font-semibold text-slate-500">
          Responsable: {createdBy}
        </p>
      )}
    </div>
  );
}

function ProformaGroupCard({ group }) {
  const proforma = group.proforma;

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-center justify-between">
        <div>
          <h4 className="font-black text-slate-900">
            {proforma.type}
          </h4>

          <p className="text-sm text-slate-500">
            Estado: {proforma.status}
          </p>
        </div>

        <div className="text-right">
          <p className="text-xs text-slate-400">Eventos</p>
          <p className="font-black text-slate-900">
            {group.timeline?.length || 0}
          </p>
        </div>
      </div>

      <div className="mt-4 grid grid-cols-3 gap-4">
        <div>
          <p className="text-xs text-slate-400">Tareas</p>
          <p className="font-bold">{group.tasks?.length || 0}</p>
        </div>

        <div>
          <p className="text-xs text-slate-400">Monto</p>
          <p className="font-bold">
            {formatMoney(proforma.total, proforma.currency || 'BOB')}
          </p>
        </div>

        <div>
          <p className="text-xs text-slate-400">Versión</p>
          <p className="font-bold">{proforma.version}</p>
        </div>
      </div>
    </div>
  );
}

function EmptyMessage({ text }) {
  return (
    <div className="rounded-2xl bg-slate-50 p-5 text-sm text-slate-500">
      {text}
    </div>
  );
}

function LeadCustomerProfilePanel({
  profile,
  cities,
  editing,
  saving,
  onEdit,
  onCancel,
  onSave,
}) {
  const [form, setForm] = useState({
    customerType: 'UNDEFINED',
    fullName: '',
    cityCode: '',
    mobilePhone: '',
    legalName: '',
    taxId: '',
    companyPhone: '',
    addressText: '',
    mapsUrl: '',
    legalRepresentativeName: '',
  });

  useEffect(() => {
    setForm({
      customerType:
        profile?.customerType || 'UNDEFINED',
      fullName: profile?.fullName || '',
      cityCode: profile?.cityCode || '',
      mobilePhone:
        profile?.mobilePhone || '',
      legalName: profile?.legalName || '',
      taxId: profile?.taxId || '',
      companyPhone:
        profile?.companyPhone || '',
      addressText:
        profile?.addressText || '',
      mapsUrl: profile?.mapsUrl || '',
      legalRepresentativeName:
        profile?.legalRepresentativeName || '',
    });
  }, [profile, editing]);

  function updateField(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  function handleSubmit(event) {
    event.preventDefault();

    if (
      form.customerType ===
      'NATURAL_PERSON'
    ) {
      if (
        !form.fullName.trim() ||
    
        !form.mobilePhone.trim()
      ) {
          alert(
            'Completa nombre y celular.'
          );
        return;
      }

      onSave({
        customerType: 'NATURAL_PERSON',
        fullName: form.fullName.trim(),
        cityCode: form.cityCode,
        mobilePhone:
          form.mobilePhone.trim(),
      });

      return;
    }

    if (form.customerType === 'COMPANY') {
      if (
        !form.legalName.trim() ||
        !form.taxId.trim() ||
        !form.companyPhone.trim() ||
      
        !form.addressText.trim() ||
        !form.legalRepresentativeName.trim()
      ) {
        alert(
          'Completa todos los datos obligatorios de la empresa.'
        );
        return;
      }

      onSave({
        customerType: 'COMPANY',
        legalName: form.legalName.trim(),
        taxId: form.taxId.trim(),
        companyPhone:
          form.companyPhone.trim(),
        cityCode: form.cityCode,
        addressText:
          form.addressText.trim(),
        mapsUrl:
          form.mapsUrl.trim() || null,
        legalRepresentativeName:
          form.legalRepresentativeName.trim(),
      });

      return;
    }

    alert(
      'Selecciona Persona natural o Empresa.'
    );
  }

  return (
    <section className="rounded-[28px] border border-slate-200 bg-white p-6 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <p className="text-xs font-black uppercase tracking-[0.25em] text-orange-500">
            Datos para proformas
          </p>

          <h3 className="mt-1 text-xl font-black text-slate-900">
            Perfil del cliente
          </h3>

          <p className="mt-1 text-sm text-slate-500">
            Información legal y comercial utilizada en las proformas.
          </p>
        </div>

        {!editing && (
          <button
            type="button"
            onClick={onEdit}
            className="rounded-2xl bg-orange-500 px-5 py-3 text-sm font-bold text-white hover:bg-orange-600"
          >
            {profile?.customerType ===
            'UNDEFINED'
              ? 'Completar perfil'
              : 'Editar datos'}
          </button>
        )}
      </div>

      {!editing && (
        <div className="mt-6">
          {profile?.customerType ===
            'NATURAL_PERSON' && (
            <div className="grid gap-5 md:grid-cols-4">
              <ProfileInfo
                label="Tipo"
                value="Persona natural"
              />

              <ProfileInfo
                label="Nombre completo"
                value={profile.fullName}
              />

              <ProfileInfo
                label="Ciudad"
                value={
                  profile.cityName
                    ? `${profile.cityName} — ${
                        profile.departmentName || ''
                      }`
                    : 'Sin información'
                }
              />

              <ProfileInfo
                label="Celular"
                value={profile.mobilePhone}
              />
            </div>
          )}

          {profile?.customerType ===
            'COMPANY' && (
            <div className="grid gap-5 md:grid-cols-3">
              <ProfileInfo
                label="Razón social"
                value={profile.legalName}
              />

              <ProfileInfo
                label="NIT"
                value={profile.taxId}
              />

              <ProfileInfo
                label="Teléfono"
                value={profile.companyPhone}
              />

              <ProfileInfo
                label="Ciudad"
                value={
                  profile.cityName
                    ? `${profile.cityName} — ${
                        profile.departmentName || ''
                      }`
                    : 'Sin información'
                }
              />

              <ProfileInfo
                label="Representante legal"
                value={
                  profile.legalRepresentativeName
                }
              />

              <ProfileInfo
                label="Dirección"
                value={profile.addressText}
              />

              {profile.mapsUrl && (
                <div>
                  <p className="text-xs font-bold uppercase tracking-wide text-slate-400">
                    Google Maps
                  </p>

                  <a
                    href={profile.mapsUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="mt-1 inline-block font-bold text-orange-600 hover:underline"
                  >
                    Abrir ubicación
                  </a>
                </div>
              )}
            </div>
          )}

          {(!profile ||
            profile.customerType ===
              'UNDEFINED') && (
            <div className="rounded-2xl border border-dashed border-orange-200 bg-orange-50 p-5 text-sm font-semibold text-orange-700">
              Debe completar el perfil del cliente antes de generar una proforma.
            </div>
          )}
        </div>
      )}

      {editing && (
        <form
          onSubmit={handleSubmit}
          className="mt-6 space-y-6"
        >
          <div className="grid gap-3 md:grid-cols-2">
            <ProfileTypeButton
              active={
                form.customerType ===
                'NATURAL_PERSON'
              }
              label="Persona natural"
              onClick={() =>
                updateField(
                  'customerType',
                  'NATURAL_PERSON'
                )
              }
            />

            <ProfileTypeButton
              active={
                form.customerType ===
                'COMPANY'
              }
              label="Empresa"
              onClick={() =>
                updateField(
                  'customerType',
                  'COMPANY'
                )
              }
            />
          </div>

          {form.customerType ===
            'NATURAL_PERSON' && (
            <div className="grid gap-4 md:grid-cols-2">
              <ProfileInput
                label="Nombre completo"
                value={form.fullName}
                onChange={(value) =>
                  updateField(
                    'fullName',
                    value
                  )
                }
              />

              <ProfileCitySelect
                cities={cities}
                value={form.cityCode}
                onChange={(value) =>
                  updateField(
                    'cityCode',
                    value
                  )
                }
              />

              <ProfileInput
                label="Número de celular"
                value={form.mobilePhone}
                onChange={(value) =>
                  updateField(
                    'mobilePhone',
                    value
                  )
                }
              />
            </div>
          )}

          {form.customerType ===
            'COMPANY' && (
            <div className="grid gap-4 md:grid-cols-2">
              <ProfileInput
                label="Razón social"
                value={form.legalName}
                onChange={(value) =>
                  updateField(
                    'legalName',
                    value
                  )
                }
              />

              <ProfileInput
                label="NIT"
                value={form.taxId}
                onChange={(value) =>
                  updateField('taxId', value)
                }
              />

              <ProfileInput
                label="Teléfono"
                value={form.companyPhone}
                onChange={(value) =>
                  updateField(
                    'companyPhone',
                    value
                  )
                }
              />

              <ProfileCitySelect
                cities={cities}
                value={form.cityCode}
                onChange={(value) =>
                  updateField(
                    'cityCode',
                    value
                  )
                }
              />

              <ProfileInput
                label="Representante legal"
                value={
                  form.legalRepresentativeName
                }
                onChange={(value) =>
                  updateField(
                    'legalRepresentativeName',
                    value
                  )
                }
              />

              <ProfileInput
                label="Dirección"
                value={form.addressText}
                onChange={(value) =>
                  updateField(
                    'addressText',
                    value
                  )
                }
              />

              <div className="md:col-span-2">
                <ProfileInput
                  label="Enlace de Google Maps"
                  value={form.mapsUrl}
                  onChange={(value) =>
                    updateField(
                      'mapsUrl',
                      value
                    )
                  }
                />
              </div>
            </div>
          )}

          <div className="flex justify-end gap-3">
            <button
              type="button"
              onClick={onCancel}
              disabled={saving}
              className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-bold text-slate-600"
            >
              Cancelar
            </button>

            <button
              type="submit"
              disabled={saving}
              className="rounded-2xl bg-orange-500 px-5 py-3 text-sm font-bold text-white disabled:opacity-60"
            >
              {saving
                ? 'Guardando...'
                : 'Guardar datos'}
            </button>
          </div>
        </form>
      )}
    </section>
  );
}

function ProfileInfo({ label, value }) {
  return (
    <div>
      <p className="text-xs font-bold uppercase tracking-wide text-slate-400">
        {label}
      </p>

      <p className="mt-1 font-bold text-slate-900">
        {value || '-'}
      </p>
    </div>
  );
}

function ProfileTypeButton({
  active,
  label,
  onClick,
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`rounded-2xl border p-4 text-left font-black ${
        active
          ? 'border-orange-500 bg-orange-50 text-orange-700'
          : 'border-slate-200 bg-white text-slate-700'
      }`}
    >
      {label}
    </button>
  );
}

function ProfileInput({
  label,
  value,
  onChange,
}) {
  return (
    <label>
      <span className="text-sm font-bold text-slate-700">
        {label}
      </span>

      <input
        value={value}
        onChange={(event) =>
          onChange(event.target.value)
        }
        className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none focus:border-orange-300"
      />
    </label>
  );
}

function ProfileCitySelect({
  cities,
  value,
  onChange,
}) {
  return (
    <label>
      <span className="text-sm font-bold text-slate-700">
        Ciudad
      </span>

      <select
        value={value}
        onChange={(event) =>
          onChange(event.target.value)
        }
        className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none focus:border-orange-300"
      >
        <option value="">
          Seleccionar ciudad
        </option>

        {cities.map((city) => (
          <option
            key={city.code}
            value={city.code}
          >
            {city.name} — {city.department}
          </option>
        ))}
      </select>
    </label>
  );
}