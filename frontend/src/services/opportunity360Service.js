import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  getOpportunity,
  getOpportunityDashboard,
  getOpportunityTimeline,
  getOpportunityTypedProformas,
} from '../services/opportunityApi';

export default function Opportunity360Page() {
  const { id } = useParams();
  const [opportunity, setOpportunity] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [timeline, setTimeline] = useState(null);
  const [proformas, setProformas] = useState([]);
  const [activeTab, setActiveTab] = useState('Resumen');
  const [loading, setLoading] = useState(true);

  const tabs = ['Resumen', 'Proformas', 'Timeline', 'Tareas', 'Conversaciones', 'Documentos'];

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        const [opp, dash, time, profs] = await Promise.all([
          getOpportunity(id),
          getOpportunityDashboard(id),
          getOpportunityTimeline(id),
          getOpportunityTypedProformas(id),
        ]);

        setOpportunity(opp);
        setDashboard(dash);
        setTimeline(time);
        setProformas(Array.isArray(profs) ? profs : []);
      } catch (error) {
        console.error('Error cargando Contacto 360 V2', error);
        alert('No se pudo cargar Contacto 360.');
      } finally {
        setLoading(false);
      }
    }

    if (id) load();
  }, [id]);

  if (loading) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-10 text-center text-slate-500">
        Cargando Contacto 360...
      </div>
    );
  }

  if (!opportunity) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-10 text-center text-slate-500">
        No se encontró la oportunidad.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <section className="rounded-[32px] border border-slate-200 bg-white p-8 shadow-sm">
        <p className="text-sm font-bold uppercase tracking-[0.3em] text-orange-500">
          Contacto 360 V2
        </p>

        <h1 className="mt-3 text-3xl font-black text-slate-900">
          {opportunity.title || 'Oportunidad sin nombre'}
        </h1>

        <p className="mt-2 text-sm text-slate-500">
          Seguimiento comercial basado en oportunidad, proformas, timeline y tareas.
        </p>

        <div className="mt-6 flex flex-wrap gap-2">
          {tabs.map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`rounded-2xl px-4 py-2 text-sm font-bold ${
                activeTab === tab
                  ? 'bg-orange-500 text-white'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {tab}
            </button>
          ))}
        </div>
      </section>

      {activeTab === 'Resumen' && (
        <section className="grid gap-5 md:grid-cols-4">
          <Card title="Etapa" value={opportunity.stage || '-'} />
          <Card title="Responsable" value={opportunity.ownerUserId || '-'} />
          <Card title="Origen" value={opportunity.source || '-'} />
          <Card title="Proformas" value={dashboard?.proformaCount ?? proformas.length} />
        </section>
      )}

      {activeTab === 'Proformas' && (
        <section className="rounded-[32px] border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-black text-slate-900">
            Historial de Proformas
          </h2>

          <div className="mt-6 space-y-3">
            {proformas.length === 0 ? (
              <Empty text="No existen proformas asociadas a esta oportunidad." />
            ) : (
              proformas.map((p) => (
                <div
                  key={p.id}
                  className="flex items-center justify-between rounded-2xl bg-slate-50 p-5"
                >
                  <div>
                    <p className="font-black text-slate-900">
                      {p.type || 'PROFORMA'} - {p.id}
                    </p>
                    <p className="mt-1 text-sm text-slate-500">
                      {p.currency || 'USD'} {Number(p.total || 0).toLocaleString()}
                    </p>
                  </div>

                  <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-bold text-slate-600">
                    {p.status || '-'}
                  </span>
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
            {(timeline?.events || []).length === 0 ? (
              <Empty text="No hay eventos registrados." />
            ) : (
              timeline.events.map((event, index) => (
                <div key={index} className="rounded-2xl bg-slate-50 p-5">
                  <p className="font-black text-slate-900">
                    {event.type || 'Evento'}
                  </p>
                  <p className="mt-1 text-sm text-slate-500">
                    {event.timestamp || '-'}
                  </p>
                  {event.reason && (
                    <p className="mt-2 text-sm text-slate-600">
                      {event.reason}
                    </p>
                  )}
                </div>
              ))
            )}
          </div>
        </section>
      )}

      {activeTab === 'Tareas' && (
        <Placeholder title="Tareas Comerciales" text="Aquí conectaremos tareas por opportunityId y proformaId." />
      )}

      {activeTab === 'Conversaciones' && (
        <Placeholder title="Conversaciones" text="Aquí se mostrará la conversación de WhatsApp vinculada al LeadInbox." />
      )}

      {activeTab === 'Documentos' && (
        <Placeholder title="Documentos" text="Aquí se adjuntarán documentos de importación, PDFs y respaldos." />
      )}
    </div>
  );
}

function Card({ title, value }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <p className="text-xs font-bold uppercase tracking-wide text-slate-400">
        {title}
      </p>
      <p className="mt-3 text-lg font-black text-slate-900">{value}</p>
    </div>
  );
}

function Empty({ text }) {
  return (
    <div className="rounded-2xl bg-slate-50 p-5 text-sm text-slate-500">
      {text}
    </div>
  );
}

function Placeholder({ title, text }) {
  return (
    <section className="rounded-[32px] border border-dashed border-slate-300 bg-white p-8 text-center">
      <h2 className="text-xl font-black text-slate-900">{title}</h2>
      <p className="mt-3 text-sm text-slate-500">{text}</p>
    </section>
  );
}