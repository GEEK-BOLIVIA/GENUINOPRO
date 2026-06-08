import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getOpportunityTasks } from '../services/tasksService';

import {
  getOpportunity,
  getOpportunityDashboard,
  getOpportunityTimeline,
  getOpportunityTypedProformas,
} from '../services/opportunityApi';

const eventLabels = {
  LEAD_CREATED: 'Contacto creado',

  OPPORTUNITY_CREATED: 'Oportunidad creada',

  CREATE: 'Registro creado',

  PROFORMA_CREATED: 'Proforma LCL generada',

  PROFORMA_SUBMITTED_FOR_APPROVAL:
    'Proforma enviada a revisión interna',

  PROFORMA_APPROVED:
    'Proforma aprobada internamente',

  PROFORMA_REJECTED:
    'Proforma rechazada internamente',

  PROFORMA_CLIENT_ACCEPTED:
    'Cliente aprobó la proforma',

  PROFORMA_CLIENT_REJECTED:
    'Cliente rechazó la proforma',

  STAGE_CHANGED:
    'Cambio de etapa comercial',

  CLIENT_CREATED:
    'Cliente creado en sistema',
};

export default function Opportunity360Page() {
  const { id } = useParams();

  const [opportunity, setOpportunity] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [timeline, setTimeline] = useState(null);
  const [proformas, setProformas] = useState([]);
  const [tasks, setTasks] = useState([]);

  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('Resumen');

  const tabs = [
    'Resumen',
    'Proformas',
    'Timeline',
    'Tareas',
    'Conversaciones',
    'Documentos',
  ];

  useEffect(() => {
    loadData();
  }, [id]);

  async function loadData() {
    try {
      setLoading(true);

      const [opp, dash, time, profs, taskList] = await Promise.all([
        getOpportunity(id),
        getOpportunityDashboard(id),
        getOpportunityTimeline(id),
        getOpportunityTypedProformas(id),
        getOpportunityTasks(id),
      ]);

      setOpportunity(opp);
      setDashboard(dash);
      setTimeline(time);
      setProformas(Array.isArray(profs) ? profs : []);
      setTasks(Array.isArray(taskList) ? taskList : []);
    } catch (error) {
      console.error('Error cargando Opportunity360', error);
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-10 text-center">
        Cargando Contacto 360...
      </div>
    );
  }

  if (!opportunity) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-10 text-center">
        No se encontró la oportunidad.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <section className="rounded-[32px] border border-slate-200 bg-white p-8 shadow-sm">
        <p className="text-sm font-bold uppercase tracking-[0.3em] text-orange-500">
          CONTACTO 360
        </p>

        <h1 className="mt-3 text-4xl font-black text-slate-900">
          {opportunity.title || 'Oportunidad'}
        </h1>

        <p className="mt-2 text-sm text-slate-500">
          Gestión integral de oportunidad, proformas, tareas y seguimiento.
        </p>

        <div className="mt-6 flex flex-wrap gap-2">
          {tabs.map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`rounded-2xl px-4 py-2 text-sm font-bold transition ${
                activeTab === tab
                  ? 'bg-orange-500 text-white'
                  : 'bg-slate-100 text-slate-600'
              }`}
            >
              {tab}
            </button>
          ))}
        </div>
      </section>

      {activeTab === 'Resumen' && (
        <>
          <div className="grid gap-5 md:grid-cols-4">
            <SummaryCard
              title="Etapa"
              value={opportunity.stage || '-'}
            />

            <SummaryCard
              title="Responsable"
              value={opportunity.ownerUserId || '-'}
            />

            <SummaryCard
              title="Cliente"
              value={opportunity.customerId || '-'}
            />

            <SummaryCard
              title="Proformas"
              value={dashboard?.proformaCount || proformas.length}
            />
          </div>

          <div className="rounded-[32px] border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-black text-slate-900">
              Resumen Comercial
            </h2>

            <div className="mt-6 grid gap-4 md:grid-cols-2">
              <InfoRow
                label="Título"
                value={opportunity.title}
              />

              <InfoRow
                label="Etapa"
                value={opportunity.stage}
              />

              <InfoRow
                label="Responsable"
                value={opportunity.ownerUserId}
              />

              <InfoRow
                label="Cliente"
                value={opportunity.customerId || 'Sin cliente'}
              />
            </div>
          </div>
        </>
      )}

      {activeTab === 'Proformas' && (
        <section className="rounded-[32px] border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-black text-slate-900">
            Historial de Proformas
          </h2>

          <div className="mt-6 space-y-3">
            {proformas.length === 0 ? (
              <EmptyMessage text="No existen proformas asociadas." />
            ) : (
              proformas.map((p) => (
                <div
                  key={p.id}
                  className="rounded-2xl border border-slate-200 p-5"
                >
                  <div className="flex items-center justify-between">
                    <div>
                        <p className="font-black text-slate-900">
                        {p.type || 'PROFORMA'}
                        </p>

                        <p className="text-sm font-semibold text-slate-600">
                        {p.customerName || 'Cliente'}
                        </p>

                        <p className="text-xs text-slate-400">
                        {p.cargoDescription || 'Producto no registrado'}
                        </p>
                    </div>

                    <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-bold">
                      {p.status}
                    </span>
                  </div>

                  <div className="mt-4 grid gap-4 md:grid-cols-4">
                    
                    <InfoRow
                        label="Total"
                        value={`Bs ${Number(p.total || 0).toLocaleString()}`}
                    />

                    <InfoRow
                        label="Asesor"
                        value={p.sellerName || '-'}
                    />

                    <InfoRow
                        label="Estado"
                        value={p.status}
                    />

                    <InfoRow
                        label="Creado"
                        value={
                        p.createdAt
                            ? new Date(p.createdAt).toLocaleString()
                            : '-'
                        }
                    />
                    </div>

                    <button
                    onClick={() => navigate(`/lcl/${p.id}`)}
                    className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-bold text-white"
                    >
                    Ver detalle
                    </button>
                  </div>
               
              ))
            )}
          </div>
        </section>
      )}

      {activeTab === 'Timeline' && (
        <section className="rounded-[32px] border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-black text-slate-900">
            Timeline Comercial
          </h2>

          <div className="mt-6 space-y-3">
            {!(timeline?.events || []).length && !(timeline?.activities || []).length ? (
              <EmptyMessage text="No existen eventos." />
            ) : (
              [...(timeline?.events || []), ...(timeline?.activities || [])].map((item, index) => (
                    <div
                    key={index}
                    className="rounded-2xl border border-slate-200 p-5"
                    >
                    <div className="flex items-start justify-between gap-4">

                        <div>
                        <p className="font-black text-slate-900">
                        {item.title ||
                            eventLabels[item.type] ||
                            item.type?.replaceAll('_', ' ')}
                        </p>

                        <p className="mt-1 text-sm text-slate-500">
                            {(item.occurredAt || item.createdAt)
                            ? new Date(
                                item.occurredAt || item.createdAt
                                ).toLocaleString()
                            : '-'}
                        </p>

                        {item.description && (
                            <p className="mt-3 text-sm text-slate-700">
                            {item.description}
                            </p>
                        )}
                        </div>

                        <span className="rounded-full bg-orange-100 px-3 py-1 text-xs font-bold text-orange-700">
                        {eventLabels[item.type] || item.type || 'EVENTO'}
                        </span>

                    </div>
                    </div>
              ))
            )}
          </div>
        </section>
      )}

    {activeTab === 'Tareas' && (
    <section className="rounded-[32px] border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex items-center justify-between">
        <h2 className="text-xl font-black text-slate-900">
            Tareas Comerciales
        </h2>

        <span className="rounded-full bg-orange-100 px-3 py-1 text-xs font-bold text-orange-700">
            {tasks.length} tareas
        </span>
        </div>

        <div className="mt-6 space-y-3">
        {tasks.length === 0 ? (
            <EmptyMessage text="No existen tareas registradas." />
        ) : (
            tasks.map((task) => (
            <div
                key={task.id}
                className="rounded-2xl border border-slate-200 p-5"
            >
                <div className="flex items-center justify-between">
                <h3 className="font-black text-slate-900">
                    {task.title}
                </h3>

                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-bold">
                    {task.priority}
                </span>
                </div>

                <p className="mt-2 text-sm text-slate-600">
                {task.description}
                </p>

                <div className="mt-4 grid gap-4 md:grid-cols-4">

                <InfoRow
                label="Proforma"
                value={task.proformaId || 'Tarea general'}
                />

                <InfoRow
                    label="Responsable"
                    value={task.assignedTo}
                />

                <InfoRow
                    label="Vence"
                    value={task.dueAt}
                />

                <InfoRow
                    label="Estado"
                    value={task.status || 'PENDIENTE'}
                />

                </div>
            </div>
            ))
        )}
        </div>
    </section>
    )}

      {activeTab === 'Conversaciones' && (
        <Placeholder
          title="Conversaciones"
          text="Próximo Sprint: integración con Inbox y WhatsApp."
        />
      )}

      {activeTab === 'Documentos' && (
        <Placeholder
          title="Documentos"
          text="Próximo Sprint: PDFs, BL, facturas y adjuntos."
        />
      )}
    </div>
  );
}

function SummaryCard({ title, value }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <p className="text-xs font-bold uppercase tracking-wide text-slate-400">
        {title}
      </p>

      <p className="mt-3 text-lg font-black text-slate-900">
        {value}
      </p>
    </div>
  );
}

function InfoRow({ label, value }) {
  return (
    <div>
      <p className="text-xs font-bold uppercase tracking-wide text-slate-400">
        {label}
      </p>

      <p className="mt-1 font-semibold text-slate-900">
        {value || '-'}
      </p>
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

function Placeholder({ title, text }) {
  return (
    <div className="rounded-[32px] border border-dashed border-slate-300 bg-white p-10 text-center">
      <h2 className="text-xl font-black text-slate-900">
        {title}
      </h2>

      <p className="mt-3 text-sm text-slate-500">
        {text}
      </p>
    </div>
  );
}