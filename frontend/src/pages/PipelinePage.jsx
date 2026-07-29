import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import {
  DndContext,
  PointerSensor,
  useDraggable,
  useDroppable,
  useSensor,
  useSensors,
} from '@dnd-kit/core';

import { CSS } from '@dnd-kit/utilities';
import {
  ArrowRight,
  BadgeDollarSign,
  Building2,
  UserCheck,
} from 'lucide-react';

import {
  getOpportunities,
  updateOpportunityStage,
} from '../services/opportunityApi';

const columns = [
  { key: 'NUEVO', label: 'Nuevo', description: 'Oportunidad recién creada desde WhatsApp.' },
  { key: 'CONTACTADO', label: 'Contactado', description: 'El vendedor ya inició contacto.' },
  { key: 'SEGUIMIENTO', label: 'Seguimiento', description: 'Cliente en proceso comercial.' },
  { key: 'REQUERIMIENTO_COMPLETO', label: 'Req. completo', description: 'Información suficiente para cotizar.' },
  { key: 'PROFORMA_GENERADA', label: 'Proforma', description: 'La proforma ya fue generada.' },
  { key: 'APROBACION_INTERNA', label: 'Aprob. interna', description: 'Pendiente de revisión interna.' },
  { key: 'APROBACION_CLIENTE', label: 'Aprob. cliente', description: 'Proforma enviada al cliente.' },
  { key: 'CLIENTE', label: 'Cliente', description: 'Cliente ganado.' },
  { key: 'PERDIDO', label: 'Perdido', description: 'Oportunidad descartada.' },
];

const columnStyles = {
  NUEVO: 'bg-sky-50 text-sky-700',
  CONTACTADO: 'bg-indigo-50 text-indigo-700',
  SEGUIMIENTO: 'bg-amber-50 text-amber-700',
  REQUERIMIENTO_COMPLETO: 'bg-cyan-50 text-cyan-700',
  PROFORMA_GENERADA: 'bg-violet-50 text-violet-700',
  APROBACION_INTERNA: 'bg-orange-50 text-orange-700',
  APROBACION_CLIENTE: 'bg-pink-50 text-pink-700',
  CLIENTE: 'bg-emerald-50 text-emerald-700',
  PERDIDO: 'bg-slate-100 text-slate-600',
};

function getDisplayName(opportunity) {
  return (
    opportunity.companyName ||
    opportunity.customerName ||
    opportunity.contactName ||
    opportunity.leadName ||
    opportunity.title ||
    'Sin nombre registrado'
  );
}

function getPhone(opportunity) {
  return (
    opportunity.customerPhone ||
    opportunity.contactPhone ||
    opportunity.phone ||
    opportunity.leadPhone ||
    'Sin teléfono'
  );
}

function getRequirement(opportunity) {
  return (
    opportunity.product ||
    opportunity.interest ||
    opportunity.requirement ||
    opportunity.notes ||
    'Sin requerimiento registrado'
  );
}

function getOwner(opportunity) {
  return (
    opportunity.ownerName ||
    opportunity.assignedToName ||
    opportunity.sellerName ||
    opportunity.ownerUserId ||
    'Sin asignar'
  );
}

export default function PipelinePage() {
  const [opportunities, setOpportunities] = useState([]);
  const [loading, setLoading] = useState(true);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 6 },
    })
  );

  useEffect(() => {
    loadOpportunities();
  }, []);

  async function loadOpportunities() {
    try {
      setLoading(true);
      const data = await getOpportunities();
      setOpportunities(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error cargando pipeline', error);
      alert('No se pudo cargar el pipeline comercial.');
    } finally {
      setLoading(false);
    }
  }

  const opportunitiesByStage = useMemo(() => {
    return columns.reduce((acc, column) => {
      acc[column.key] = opportunities.filter(
        (opportunity) => opportunity.stage === column.key
      );
      return acc;
    }, {});
  }, [opportunities]);

  const summary = useMemo(() => {
    const activeStages = [
      'NUEVO',
      'CONTACTADO',
      'SEGUIMIENTO',
      'REQUERIMIENTO_COMPLETO',
      'PROFORMA_GENERADA',
      'APROBACION_INTERNA',
      'APROBACION_CLIENTE',
    ];

    const active = opportunities.filter((opportunity) =>
      activeStages.includes(opportunity.stage)
    ).length;

    const quoted = opportunities.filter(
      (opportunity) => opportunity.stage === 'PROFORMA_GENERADA'
    ).length;

    const won = opportunities.filter(
      (opportunity) => opportunity.stage === 'CLIENTE'
    ).length;

    return { active, quoted, won };
  }, [opportunities]);

  async function handleDragEnd(event) {
    const { active, over } = event;

    if (!over) return;

    const opportunityId = active.id;
    const newStage = over.id;

    const currentOpportunity = opportunities.find(
      (opportunity) => opportunity.id === opportunityId
    );

    if (!currentOpportunity || currentOpportunity.stage === newStage) return;

    setOpportunities((prev) =>
      prev.map((opportunity) =>
        opportunity.id === opportunityId
          ? { ...opportunity, stage: newStage }
          : opportunity
      )
    );

    try {
      const updated = await updateOpportunityStage(opportunityId, newStage);

      setOpportunities((prev) =>
        prev.map((opportunity) =>
          opportunity.id === opportunityId ? updated : opportunity
        )
      );
    } catch (error) {
      console.error('Error actualizando etapa', error);

      setOpportunities((prev) =>
        prev.map((opportunity) =>
          opportunity.id === opportunityId
            ? { ...opportunity, stage: currentOpportunity.stage }
            : opportunity
        )
      );

      alert('No se pudo mover la oportunidad.');
    }
  }

  return (
    <div className="space-y-6">
      <section className="flex flex-col justify-between gap-4 xl:flex-row xl:items-end">
        <div>
          <p className="text-sm font-medium text-slate-500">
            Gestión comercial
          </p>

          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Pipeline
          </h1>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Vista Kanban de oportunidades comerciales. Aquí se mueve cada
            operación desde WhatsApp hasta cliente ganado.
          </p>
        </div>

        <div className="grid gap-3 sm:grid-cols-3">
          <SummaryPill icon={ArrowRight} label="Abiertas" value={summary.active} />
          <SummaryPill icon={BadgeDollarSign} label="Proformas" value={summary.quoted} />
          <SummaryPill icon={UserCheck} label="Clientes" value={summary.won} />
        </div>
      </section>

      {loading ? (
        <div className="rounded-3xl border border-slate-200 bg-white p-10 text-center text-sm text-slate-500">
          Cargando pipeline comercial...
        </div>
      ) : (
        <DndContext sensors={sensors} onDragEnd={handleDragEnd}>
        <div
          className="
              flex
              gap-5
              overflow-x-auto
              overflow-y-hidden
              pb-6
              pr-4
              scrollbar-thin
              scrollbar-thumb-slate-300
              scrollbar-track-transparent
          "
        >
            {columns.map((column) => (
              <PipelineColumn
                key={column.key}
                id={column.key}
                title={column.label}
                description={column.description}
                tone={columnStyles[column.key]}
                opportunities={opportunitiesByStage[column.key] || []}
              />
            ))}
          </div>
        </DndContext>
      )}
    </div>
  );
}

function SummaryPill({ icon: Icon, label, value }) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white px-5 py-4 shadow-sm">
      <div className="flex items-center gap-3">
        <div className="rounded-xl bg-orange-50 p-2 text-orange-600">
          <Icon size={18} />
        </div>

        <div>
          <p className="text-xs font-bold uppercase tracking-wide text-slate-400">
            {label}
          </p>

          <p className="text-xl font-black text-slate-900">{value}</p>
        </div>
      </div>
    </div>
  );
}

function PipelineColumn({ id, title, description, tone, opportunities }) {
  const { setNodeRef, isOver } = useDroppable({ id });

  return (
    <section
      ref={setNodeRef}
      className={`flex h-[calc(100vh-310px)] min-h-[620px] w-[340px] shrink-0 flex-col overflow-hidden rounded-[28px] border transition ${
        isOver
          ? 'border-orange-300 bg-orange-50'
          : 'border-slate-200 bg-white'
      } shadow-sm`}
    >
      <header className="shrink-0 border-b border-slate-200 bg-white px-4 py-4">
        <div className="flex items-center justify-between gap-3">
          <span
            className={`rounded-full px-3 py-1 text-xs font-black ${tone}`}
          >
            {title}
          </span>

          <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-bold text-slate-600">
            {opportunities.length}
          </span>
        </div>

        <p className="mt-3 text-xs leading-5 text-slate-400">
          {description}
        </p>
      </header>

      <div className="min-h-0 flex-1 space-y-4 overflow-y-auto px-4 py-4">
        {opportunities.map((opportunity) => (
          <PipelineCard
            key={opportunity.id}
            opportunity={opportunity}
          />
        ))}

        {opportunities.length === 0 && (
          <div className="rounded-3xl border border-dashed border-slate-200 bg-slate-50 p-6 text-center">
            <p className="text-sm font-bold text-slate-500">
              Sin oportunidades
            </p>

            <p className="mt-1 text-xs leading-5 text-slate-400">
              Arrastra aquí una tarjeta cuando avance a esta etapa.
            </p>
          </div>
        )}
      </div>
    </section>
  );
}

function PipelineCard({ opportunity }) {
  const navigate = useNavigate();
  const { attributes, listeners, setNodeRef, transform, isDragging } =
    useDraggable({
      id: opportunity.id,
    });

  const style = {
    transform: CSS.Translate.toString(transform),
    zIndex: isDragging ? 50 : 'auto',
  };

 const displayName = getDisplayName(opportunity);
 const phone = getPhone(opportunity);
 const requirement = getRequirement(opportunity);
 const owner = getOwner(opportunity);

  return (
    <article
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
      className={`cursor-grab rounded-3xl border border-slate-200 bg-slate-50 p-4 shadow-sm transition active:cursor-grabbing ${
        isDragging ? 'scale-105 opacity-80 shadow-xl' : 'hover:-translate-y-1 hover:shadow-md'
      }`}
    >
      <div className="flex items-start justify-between gap-3">

        <div className="min-w-0 flex-1">
          <p className="whitespace-normal break-words text-base font-black leading-5 text-slate-900">
            {displayName}
          </p>

          <p className="mt-1 text-sm font-semibold text-slate-500">
            {phone}
          </p>

          <p className="mt-1 text-xs text-slate-400">
            {opportunity.createdAt
              ? new Date(opportunity.createdAt).toLocaleString()
              : ''}
          </p>
        </div>

        <span className="rounded-full bg-white px-2.5 py-1 text-[11px] font-bold text-orange-700">
          {opportunity.source || 'CRM'}
        </span>
      </div>

        <div className="mt-4 space-y-2">

          <div>
            <p className="text-xs font-bold uppercase text-slate-400">
              Requerimiento
            </p>

            <p className="text-sm text-slate-600">
              {requirement}
            </p>
          </div>

          <div>
            <p className="text-xs font-bold uppercase text-slate-400">
              Responsable
            </p>

            <p className="text-sm font-semibold text-slate-700">
              {owner}
            </p>
          </div>

        </div>

      <div className="mt-5 space-y-2 border-t border-slate-200 pt-4">
        <div className="flex items-center justify-between gap-3 text-xs">
          <span className="flex items-center gap-1 font-bold text-slate-700">
            <UserCheck size={13} className="text-emerald-500" />
            {owner}
          </span>

          <span className="font-semibold text-slate-400">
            {opportunity.stage || '-'}
          </span>
        </div>

        <div className="flex items-center gap-1 text-xs font-semibold text-slate-400">
          <Building2 size={13} />
          {opportunity.customerId ? 'Cliente vinculado' : 'Contacto pendiente'}
        </div>
        <button
          type="button"
          onClick={(e) => {
            e.stopPropagation();
            navigate(`/opportunities/${opportunity.id}`);
          }}
          className="mt-4 w-full rounded-xl bg-orange-500 px-3 py-2 text-sm font-bold text-white hover:bg-orange-600"
        >
          Ver Contacto 360
        </button>
      </div>
    </article>
  );
}